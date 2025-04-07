package com.example.expensetrackingsystem.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
public class Budget {

    private float budgetAmount;

    private LocalDate startDate;

    private LocalDate endDate;

    @Id
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}
