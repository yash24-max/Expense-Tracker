package com.expensetracker.controller;

import com.expensetracker.dto.ExpenseDto;
import com.expensetracker.service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    @PostMapping
    public ResponseEntity<?> createExpense(@RequestBody ExpenseDto expenseDto) {
        return expenseService.addExpense(expenseDto);

    }

    @PutMapping("{id}")
    public ResponseEntity<?> updateExpense(@PathVariable("id") Long id, @RequestBody ExpenseDto expenseDto) {
        return expenseService.updateExpense(id, expenseDto);
    }

    @GetMapping
    public ResponseEntity<?> getAllExpenses() {
        return expenseService.getAllExpenses();
    }

    @GetMapping("/filter")
    public ResponseEntity<?> getFiltered(@RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime start,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime end) {
        return expenseService.getExpensesBetween(start, end);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteExpense(@PathVariable("id") Long id) {
        return expenseService.deleteExpense(id);
    }

}
