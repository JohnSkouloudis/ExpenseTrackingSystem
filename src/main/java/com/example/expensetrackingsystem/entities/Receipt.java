package com.example.expensetrackingsystem.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Data
@Entity
public class Receipt {

    private String imagePath;

    @Id
    @OneToOne
    @JoinColumn(name = "transaction_id")
    private Transaction transaction;
}
