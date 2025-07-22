package com.example.expensetrackingsystem.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ScheduledTransaction {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;


    private String description;


    private float amount;


    private LocalDate createdDate;


    private int frequencyInDays;


    private LocalDate executionDate;


    private int accountId;


    private String categoryName;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
