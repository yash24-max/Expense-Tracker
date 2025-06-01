package com.expensetracker;

import com.expensetracker.dto.ExpenseDto;
import com.expensetracker.entity.Expense;
import com.expensetracker.entity.User;
import com.expensetracker.enumeration.ExpenseCategory;
import com.expensetracker.repository.ExpenseRepository;
import com.expensetracker.repository.UserRepository;
import com.expensetracker.service.ExpenseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ExpenseServiceTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private ExpenseService expenseService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testuser");
    }

    @Test
    void testAddExpense() {
        ExpenseDto dto = new ExpenseDto();
        dto.setAmount(100.00);
        dto.setCategory(ExpenseCategory.CLOTHING);
        dto.setDescription("Dress");
        dto.setDate(LocalDateTime.now());

        User user = new User();
        user.setId(1L);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(expenseRepository.save(any(Expense.class))).thenAnswer(i -> i.getArguments()[0]);

        ResponseEntity<?> response = expenseService.addExpense(dto);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
    }

    @Test
    void testGetAllExpenses() {
        User user = new User();
        user.setId(1L);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(expenseRepository.findByUserId(user.getId())).thenReturn(Collections.emptyList());

        ResponseEntity<?> response = expenseService.getAllExpenses();

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
    }

    @Test
    void testDeleteExpense_NotFound() {
        when(expenseRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = expenseService.deleteExpense(1L);

        assertThat(response.getStatusCodeValue()).isEqualTo(404);
    }

    @Test
    void testDeleteExpense_Success() {
        Expense expense = new Expense();
        expense.setId(1L);

        when(expenseRepository.findById(1L)).thenReturn(Optional.of(expense));

        ResponseEntity<?> response = expenseService.deleteExpense(1L);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        verify(expenseRepository, times(1)).delete(expense);
    }

    // You can similarly write tests for updateExpense() and getExpensesBetween()
}