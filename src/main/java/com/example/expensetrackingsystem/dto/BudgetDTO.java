package com.example.expensetrackingsystem.dto;

import com.example.expensetrackingsystem.entities.DurationEnum;

import java.time.LocalDate;

public record BudgetDTO(

     float budgetAmount,
     LocalDate startDate,
     int duration,
     DurationEnum durationUnit

) {
}
