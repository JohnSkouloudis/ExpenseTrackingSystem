package com.example.expensetrackingsystem.dto;

import java.time.LocalDate;

public record AccountDTO(

        int id,
        float balance,
        String accountName,
        int userId
) {
}
