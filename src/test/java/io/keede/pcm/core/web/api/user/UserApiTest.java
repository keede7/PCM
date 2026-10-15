package io.keede.pcm.core.web.api.user;

import io.keede.pcm.core.web.api.ApiTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserApiTest extends ApiTestSupport {

    @Test
    @DisplayName("[시나리오 1] 회원가입 → 로그인 → 발급받은 토큰으로 /users/me 조회 성공")
    void signupLoginAndGetMe() throws Exception {
        String email = uniqueEmail();

        mockMvc.perform(post("/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email": "%s", "password": "%s", "name": "keede"}
                                """.formatted(email, PASSWORD)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(notNullValue()))
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.password").doesNotExist());

        String token = login(email);

        mockMvc.perform(get("/users/me")
                        .header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.name").value("keede"));
    }

    @Test
    @DisplayName("[시나리오 3] 토큰 없이 /users/me 를 요청하면 401")
    void getMeWithoutToken() throws Exception {
        mockMvc.perform(get("/users/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("유효하지 않은 토큰으로 요청하면 401")
    void getMeWithInvalidToken() throws Exception {
        mockMvc.perform(get("/users/me")
                        .header(HttpHeaders.AUTHORIZATION, bearer("invalid.token.value")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("이미 가입된 이메일로 회원가입하면 409")
    void signupWithDuplicateEmail() throws Exception {
        String email = uniqueEmail();
        signup(email, "keede");

        mockMvc.perform(post("/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email": "%s", "password": "%s", "name": "keede"}
                                """.formatted(email, PASSWORD)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("잘못된 비밀번호로 로그인하면 401")
    void loginWithWrongPassword() throws Exception {
        String email = uniqueEmail();
        signup(email, "keede");

        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email": "%s", "password": "wrong-password"}
                                """.formatted(email)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("회원가입 요청값이 올바르지 않으면 400")
    void signupWithInvalidRequest() throws Exception {
        mockMvc.perform(post("/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email": "not-an-email", "password": "short", "name": ""}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("[선택] 내 정보를 수정하면 변경된 이름이 조회되고 새 비밀번호로 로그인할 수 있다")
    void updateMe() throws Exception {
        String email = uniqueEmail();
        signup(email, "keede");
        String token = login(email);

        mockMvc.perform(put("/users/me")
                        .header(HttpHeaders.AUTHORIZATION, bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "new-name", "password": "newPassword123"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("new-name"));

        mockMvc.perform(get("/users/me")
                        .header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("new-name"));

        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email": "%s", "password": "newPassword123"}
                                """.formatted(email)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").value(notNullValue()));
    }

    @Test
    @DisplayName("[선택] 내 정보 수정 요청값이 올바르지 않으면 400")
    void updateMeWithInvalidRequest() throws Exception {
        String token = signupAndLogin();

        mockMvc.perform(put("/users/me")
                        .header(HttpHeaders.AUTHORIZATION, bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "", "password": "short"}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("[선택] 회원 탈퇴 시 발급된 모든 토큰이 만료되어 401이 반환되고, 로그인도 불가하다")
    void withdraw() throws Exception {
        String email = uniqueEmail();
        signup(email, "keede");
        String token = login(email);
        String otherDeviceToken = login(email);

        mockMvc.perform(post("/todos")
                        .header(HttpHeaders.AUTHORIZATION, bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title": "탈퇴 전 TODO"}
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(delete("/users/me")
                        .header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/users/me")
                        .header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));

        mockMvc.perform(get("/todos")
                        .header(HttpHeaders.AUTHORIZATION, bearer(otherDeviceToken)))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email": "%s", "password": "%s"}
                                """.formatted(email, PASSWORD)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("[선택] 토큰 없이 내 정보 수정/탈퇴를 요청하면 401")
    void updateAndWithdrawWithoutToken() throws Exception {
        mockMvc.perform(put("/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "new-name"}
                                """))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(delete("/users/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("사용자 응답의 생성일시는 yyyy-MM-dd HH:mm:ss 형식이다")
    void dateTimeResponseFormat() throws Exception {
        String email = uniqueEmail();

        mockMvc.perform(post("/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email": "%s", "password": "%s", "name": "keede"}
                                """.formatted(email, PASSWORD)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.createdAt").value(matchesPattern(DATE_TIME_PATTERN)));
    }

    @Test
    @DisplayName("[선택] 탈퇴한 이메일로 재가입하면 새로 발급받은 토큰은 정상 사용 가능하다")
    void signupAgainAfterWithdraw() throws Exception {
        String email = uniqueEmail();
        signup(email, "keede");
        String oldToken = login(email);

        mockMvc.perform(delete("/users/me")
                        .header(HttpHeaders.AUTHORIZATION, bearer(oldToken)))
                .andExpect(status().isNoContent());

        signup(email, "keede-again");
        String newToken = login(email);

        mockMvc.perform(get("/users/me")
                        .header(HttpHeaders.AUTHORIZATION, bearer(newToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("keede-again"));

        mockMvc.perform(get("/users/me")
                        .header(HttpHeaders.AUTHORIZATION, bearer(oldToken)))
                .andExpect(status().isUnauthorized());
    }
}
