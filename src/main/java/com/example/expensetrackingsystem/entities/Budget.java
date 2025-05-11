package com.example.expensetrackingsystem.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
public class Budget {

    @Id
    private Long id;

    private float budgetAmount;

    private float remainingAmount;

    private LocalDate startDate;

    private LocalDate endDate;


    @OneToOne
    @PrimaryKeyJoinColumn(name = "user_id")
    private User user;
}
