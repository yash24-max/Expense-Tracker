package com.expensetracker;

import com.expensetracker.dto.ExpenseDto;
import com.expensetracker.dto.RequestDto;
import com.expensetracker.enumeration.ExpenseCategory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.*;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ExpenseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static String jwtToken;
    private static Long   createdExpenseId;

    private final String username = "testuser";
    private final String password = "testpass";

    @Test
    @Order(1)
    void testSignupAndLogin() throws Exception {
//        // Signup
//        mockMvc.perform(post("/api/auth/signup").contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(new RequestDto(username, password)))).andExpect(status().isOk());

        // Login
        MvcResult result = mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RequestDto(username, password)))).andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists()).andReturn();

        jwtToken = objectMapper.readTree(result.getResponse().getContentAsString()).get("token").asText();
        Assertions.assertNotNull(jwtToken);
    }

    @Test
    @Order(2)
    void testCreateExpense() throws Exception {
        ExpenseDto expense = ExpenseDto.builder().description("Lunch").amount(15.50).date(LocalDateTime.now()).category(ExpenseCategory.OTHERS)
                .build();

        MvcResult result = mockMvc.perform(post("/api/expenses").header("Authorization", "Bearer " + jwtToken).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(expense))).andExpect(status().isOk()).andExpect(jsonPath("$.id").exists()).andReturn();

        createdExpenseId = objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();
    }

    @Test
    @Order(3)
    void testGetAllExpenses() throws Exception {
        mockMvc.perform(get("/api/expenses").header("Authorization", "Bearer " + jwtToken)).andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @Order(4)
    void testUpdateExpense() throws Exception {
        ExpenseDto updated = ExpenseDto.builder().description("Dinner").amount(25.0).date(LocalDateTime.now()).category(ExpenseCategory.OTHERS)
                .build();

        mockMvc.perform(put("/api/expenses/" + createdExpenseId).header("Authorization", "Bearer " + jwtToken).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updated))).andExpect(status().isOk()).andExpect(jsonPath("$.description").value("Dinner"));
    }

    @Test
    @Order(5)
    void testDeleteExpense() throws Exception {
        mockMvc.perform(delete("/api/expenses/" + createdExpenseId).header("Authorization", "Bearer " + jwtToken)).andExpect(status().isOk());
    }

    @Test
    @Order(6)
    void testGetFilteredExpenses() throws Exception {
        String now = LocalDateTime.now().toString();
        mockMvc.perform(get("/api/expenses/filter").header("Authorization", "Bearer " + jwtToken).param("start", now).param("end", now))
                .andExpect(status().isOk());
    }
}