package com.example.expensetrackingsystem.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String username;

    private String password;

    private String email;

    @OneToMany(mappedBy = "user",orphanRemoval = true)
    private List<Account> accounts;
}
