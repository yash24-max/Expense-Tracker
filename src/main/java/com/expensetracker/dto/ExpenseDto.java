package com.expensetracker.dto;

import com.expensetracker.enumeration.ExpenseCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseDto {
    private Long            id;
    private String          description;
    private Double          amount;
    private LocalDateTime   date;
    private ExpenseCategory category;
}
