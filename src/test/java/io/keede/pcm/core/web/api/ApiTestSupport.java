package io.keede.pcm.core.web.api;

import com.jayway.jsonpath.JsonPath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public abstract class ApiTestSupport {

    protected static final String PASSWORD = "password123";
    protected static final String DATE_TIME_PATTERN = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$";

    @Autowired
    protected MockMvc mockMvc;

    protected String uniqueEmail() {
        return UUID.randomUUID() + "@pcm.io";
    }

    protected void signup(
            final String email,
            final String name
    ) throws Exception {
        mockMvc.perform(post("/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email": "%s", "password": "%s", "name": "%s"}
                                """.formatted(email, PASSWORD, name)))
                .andExpect(status().isCreated());
    }

    protected String login(
            final String email
    ) throws Exception {
        String response = mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email": "%s", "password": "%s"}
                                """.formatted(email, PASSWORD)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return JsonPath.read(response, "$.access_token");
    }

    protected String signupAndLogin() throws Exception {
        String email = uniqueEmail();
        signup(email, "keede");
        return login(email);
    }

    protected String bearer(
            final String token
    ) {
        return "Bearer " + token;
    }
}
