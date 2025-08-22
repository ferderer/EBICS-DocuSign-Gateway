package de.ferderer.ebicsdocusign.gateway.common.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.ferderer.ebicsdocusign.gateway.App;
import jakarta.servlet.http.Cookie;
import java.util.Arrays;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.*;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.*;
import org.springframework.transaction.annotation.Transactional;

@AutoConfigureMockMvc
@SpringBootTest(classes = App.class)
@AutoConfigureTestEntityManager
@Transactional
abstract public class IntegrationTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;
    
    protected ResultActions perform(RequestBuilder requestBuilder) throws Exception {
        return mockMvc.perform(requestBuilder);
    }

    protected MockHttpServletRequestBuilder get(String uri, Object... vars) {
        return MockMvcRequestBuilders.get(uri, vars);
    }

    protected MockHttpServletRequestBuilder post(String uri, Object... vars) {
        return MockMvcRequestBuilders.post(uri, vars);
    }

    protected MockHttpServletRequestBuilder put(String uri, Object... vars) {
        return MockMvcRequestBuilders.put(uri, vars);
    }

    protected MockHttpServletRequestBuilder delete(String uri, Object... vars) {
        return MockMvcRequestBuilders.delete(uri, vars);
    }

    protected MockHttpServletRequestBuilder multipart(String uri, Object... vars) {
        return MockMvcRequestBuilders.multipart(uri, vars);
    }

    protected StatusResultMatchers status() {
        return MockMvcResultMatchers.status();
    }

    protected ContentResultMatchers content() {
        return MockMvcResultMatchers.content();
    }

    protected ResultHandler print() {
        return MockMvcResultHandlers.print();
    }

    protected Optional<String> sid(MvcResult result) {
        return Arrays.stream(result.getResponse().getCookies())
            .filter(cookie -> "SID".equals(cookie.getName()))
            .map(Cookie::getValue)
            .findFirst();
    }

    protected <T> String json(T content) throws JsonProcessingException {
        return om.writeValueAsString(content);
    }

    protected <T> T convertJson(String content, Class<T> cls) throws JsonProcessingException {
        return om.readValue(content, cls);
    }
}
