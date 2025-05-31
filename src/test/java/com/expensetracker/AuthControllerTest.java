package com.expensetracker;

import com.expensetracker.dto.RequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final String username = "testuser";
    private final String password = "testpass";

    private static String jwtToken;

    @Test
    @Order(1)
    void testSignup() throws Exception {
        RequestDto request = new RequestDto();
        request.setUsername(username);
        request.setPassword(password);

        mockMvc.perform(post("/api/auth/signup").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @Order(2)
    void testLogin() throws Exception {
        RequestDto request = new RequestDto();
        request.setUsername(username);
        request.setPassword(password);

        MvcResult result = mockMvc.perform(
                        post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.token").exists()).andReturn();

        // Extract JWT token
        String responseBody = result.getResponse().getContentAsString();
        jwtToken = objectMapper.readTree(responseBody).get("token").asText();
        Assertions.assertNotNull(jwtToken);
    }

    @Test
    @Order(3)
    void testAccessProtectedEndpoint() throws Exception {
        // Replace with a real protected endpoint once Expense API is created
        mockMvc.perform(get("/api/expenses")  // Adjust to your protected route
                .header("Authorization", "Bearer " + jwtToken)).andExpect(status().isOk());
    }
}