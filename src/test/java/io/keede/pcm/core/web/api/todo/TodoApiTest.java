package io.keede.pcm.core.web.api.todo;

import com.jayway.jsonpath.JsonPath;
import io.keede.pcm.core.web.api.ApiTestSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TodoApiTest extends ApiTestSupport {

    private static final long NOT_EXIST_TODO_ID = Long.MAX_VALUE;
    private static final String STORED_DATE_TIME_PATTERN = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}$";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @DisplayName("[시나리오 2] TODO 생성 → 목록 조회 → 수정 → 삭제")
    void todoLifecycle() throws Exception {
        String token = signupAndLogin();

        // 생성
        String created = mockMvc.perform(post("/todos")
                        .header(HttpHeaders.AUTHORIZATION, bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title": "장보기", "content": "우유, 계란"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("장보기"))
                .andExpect(jsonPath("$.completed").value(false))
                .andReturn()
                .getResponse()
                .getContentAsString();
        long todoId = ((Number) JsonPath.read(created, "$.id")).longValue();

        // 목록 조회
        mockMvc.perform(get("/todos")
                        .header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(todoId))
                .andExpect(jsonPath("$[0].title").value("장보기"));

        // 수정
        mockMvc.perform(put("/todos/{id}", todoId)
                        .header(HttpHeaders.AUTHORIZATION, bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title": "장보기 완료", "content": "우유, 계란, 빵", "completed": true}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("장보기 완료"))
                .andExpect(jsonPath("$.content").value("우유, 계란, 빵"))
                .andExpect(jsonPath("$.completed").value(true));

        // 단건 조회로 수정 반영 확인
        mockMvc.perform(get("/todos/{id}", todoId)
                        .header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("장보기 완료"))
                .andExpect(jsonPath("$.completed").value(true));

        // 삭제
        mockMvc.perform(delete("/todos/{id}", todoId)
                        .header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/todos")
                        .header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("[시나리오 3] 토큰 없이 TODO API를 요청하면 401")
    void requestWithoutToken() throws Exception {
        mockMvc.perform(get("/todos"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title": "장보기"}
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("[시나리오 4] 존재하지 않는 TODO ID로 요청하면 404")
    void requestWithNotExistTodoId() throws Exception {
        String token = signupAndLogin();

        mockMvc.perform(get("/todos/{id}", NOT_EXIST_TODO_ID)
                        .header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("TODO_NOT_FOUND"));

        mockMvc.perform(put("/todos/{id}", NOT_EXIST_TODO_ID)
                        .header(HttpHeaders.AUTHORIZATION, bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title": "수정", "completed": true}
                                """))
                .andExpect(status().isNotFound());

        mockMvc.perform(delete("/todos/{id}", NOT_EXIST_TODO_ID)
                        .header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("다른 사용자의 TODO는 조회할 수 없다 (404)")
    void cannotAccessOthersTodo() throws Exception {
        String ownerToken = signupAndLogin();
        String otherToken = signupAndLogin();

        String created = mockMvc.perform(post("/todos")
                        .header(HttpHeaders.AUTHORIZATION, bearer(ownerToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title": "비밀 TODO"}
                                """))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        long todoId = ((Number) JsonPath.read(created, "$.id")).longValue();

        mockMvc.perform(get("/todos/{id}", todoId)
                        .header(HttpHeaders.AUTHORIZATION, bearer(otherToken)))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/todos")
                        .header(HttpHeaders.AUTHORIZATION, bearer(otherToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("제목 없이 TODO를 생성하면 400")
    void createWithoutTitle() throws Exception {
        String token = signupAndLogin();

        mockMvc.perform(post("/todos")
                        .header(HttpHeaders.AUTHORIZATION, bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title": " "}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("[선택] 제목에 키워드가 포함된 내 TODO만 검색된다")
    void search() throws Exception {
        String token = signupAndLogin();
        String otherToken = signupAndLogin();
        createTodo(token, "장보기");
        createTodo(token, "운동하기");
        createTodo(token, "주말 장보기");
        createTodo(otherToken, "남의 장보기");

        mockMvc.perform(get("/todos/search")
                        .param("keyword", "장보기")
                        .header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title").value("장보기"))
                .andExpect(jsonPath("$[1].title").value("주말 장보기"));
    }

    @Test
    @DisplayName("[선택] 검색 키워드가 없거나 비어있으면 400")
    void searchWithoutKeyword() throws Exception {
        String token = signupAndLogin();

        mockMvc.perform(get("/todos/search")
                        .header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/todos/search")
                        .param("keyword", " ")
                        .header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("[선택] 토큰 없이 검색하면 401")
    void searchWithoutToken() throws Exception {
        mockMvc.perform(get("/todos/search").param("keyword", "장보기"))
                .andExpect(status().isUnauthorized());
    }

    private void createTodo(
            final String token,
            final String title
    ) throws Exception {
        mockMvc.perform(post("/todos")
                        .header(HttpHeaders.AUTHORIZATION, bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title": "%s"}
                                """.formatted(title)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("TODO의 생성/수정일시는 yyyy-MM-ddTHH:mm:ss 형식으로 저장되고 yyyy-MM-dd HH:mm:ss 형식으로 응답된다")
    void dateTimeFormat() throws Exception {
        String token = signupAndLogin();

        String created = mockMvc.perform(post("/todos")
                        .header(HttpHeaders.AUTHORIZATION, bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title": "장보기"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.createdAt").value(matchesPattern(DATE_TIME_PATTERN)))
                .andExpect(jsonPath("$.updatedAt").value(matchesPattern(DATE_TIME_PATTERN)))
                .andReturn()
                .getResponse()
                .getContentAsString();
        long todoId = ((Number) JsonPath.read(created, "$.id")).longValue();

        Map<String, Object> stored = jdbcTemplate.queryForMap(
                "SELECT created_at, updated_at FROM todos WHERE id = ?", todoId);

        assertThat(stored.get("created_at")).asString().matches(STORED_DATE_TIME_PATTERN);
        assertThat(stored.get("updated_at")).asString().matches(STORED_DATE_TIME_PATTERN);
    }
}
