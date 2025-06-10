package com.example.expensetrackingsystem.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
@Setter
public class AccountDTO {

    private int id;
    private float balance;
    private String accountName;
    private int userId;
}
