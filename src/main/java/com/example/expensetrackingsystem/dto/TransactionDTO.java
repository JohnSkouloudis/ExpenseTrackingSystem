package com.example.expensetrackingsystem.dto;

import java.time.LocalDate;

public record TransactionDTO(

        int id,
        float amount,
        String description,
        LocalDate date,
        int accountId,
        String categoryName
) {}
