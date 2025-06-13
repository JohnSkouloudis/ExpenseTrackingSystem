package com.example.expensetrackingsystem.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Account {

 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private int id;

 private float balance;

 private String accountName;


@JsonIgnore
@ManyToOne
@JoinColumn(name = "user_id",nullable = false)
 private User user;




}
