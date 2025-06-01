package com.expensetracker.dto;

import com.expensetracker.enumeration.ExpenseCategory;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ExpenseDto {
    private Long            id;
    private String          description;
    private Double          amount;
    private LocalDateTime   date;
    private ExpenseCategory category;
}
