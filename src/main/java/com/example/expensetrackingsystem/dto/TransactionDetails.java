package com.example.expensetrackingsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class TransactionDetails {

    private TransactionDTO transactiondto;
    private String base64Image;
}
