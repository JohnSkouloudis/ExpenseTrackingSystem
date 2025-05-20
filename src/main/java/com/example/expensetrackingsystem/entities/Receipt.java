package com.example.expensetrackingsystem.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Receipt {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String imageName;


    @OneToOne
    @JoinColumn(name = "transaction_id")
    private Transaction transaction;
}
