package com.expensetracker.service;

import com.expensetracker.dto.ExpenseDto;
import com.expensetracker.entity.Expense;
import com.expensetracker.entity.User;
import com.expensetracker.repository.ExpenseRepository;
import com.expensetracker.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ExpenseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExpenseService.class);

    @Autowired
    private ExpenseRepository expenseRepository;
    @Autowired
    private UserRepository    userRepository;

    private String getCurrentUser() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public ResponseEntity<?> addExpense(ExpenseDto expenseDto) {
        try {
            User user = userRepository.findByUsername(getCurrentUser()).orElseThrow(() -> new NoSuchElementException("User not found"));
            Expense expense = Expense.builder().description(expenseDto.getDescription()).amount(expenseDto.getAmount()).date(expenseDto.getDate())
                    .category(expenseDto.getCategory()).userId(user.getId()).build();
            expense = expenseRepository.save(expense);
            return new ResponseEntity<>(toDTO(expense), HttpStatus.OK);
        } catch (NoSuchElementException e) {
            LOGGER.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Error adding expense", e);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }

    public ResponseEntity<?> updateExpense(Long id, ExpenseDto expenseDto) {
        try {
            Expense expense = expenseRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Expense not found"));
            expense.setDescription(expenseDto.getDescription());
            expense.setAmount(expenseDto.getAmount());
            expense.setDate(expenseDto.getDate());
            expense.setCategory(expenseDto.getCategory());
            return new ResponseEntity<>(toDTO(expense), HttpStatus.OK);
        } catch (NoSuchElementException e) {
            LOGGER.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Error updating expense", e);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }

    public ResponseEntity<?> getAllExpenses() {
        try {
            User user = userRepository.findByUsername(getCurrentUser()).orElseThrow(() -> new NoSuchElementException("User not found"));
            List<?> expenseList = expenseRepository.findByUserId(user.getId()).stream().map(this::toDTO).toList();
            return new ResponseEntity<>(expenseList, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            LOGGER.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Error fetching all expenses", e);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }

    public ResponseEntity<?> getExpensesBetween(LocalDateTime start, LocalDateTime end) {
        try {
            User user = userRepository.findByUsername(getCurrentUser()).orElseThrow(() -> new NoSuchElementException("User not found"));
            List<?> expenseList = expenseRepository.findByUserIdAndDateBetween(user.getId(), start, end).stream().map(this::toDTO).toList();
            return new ResponseEntity<>(expenseList, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            LOGGER.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Error fetching expenses between dates", e);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }

    public ResponseEntity<?> deleteExpense(Long id) {
        try {
            Expense expense = expenseRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Expense not found"));
            expenseRepository.delete(expense);
            return new ResponseEntity<>("Expense deleted successfully", HttpStatus.OK);
        } catch (NoSuchElementException e) {
            LOGGER.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Error deleting expense", e);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }

    private ExpenseDto toDTO(Expense expense) {
        ExpenseDto dto = new ExpenseDto();
        dto.setId(expense.getId());
        dto.setDescription(expense.getDescription());
        dto.setAmount(expense.getAmount());
        dto.setDate(expense.getDate());
        dto.setCategory(expense.getCategory());
        return dto;
    }
}
