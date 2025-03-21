package com.example.expensetrackingsystem.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String categoryName;

    @ManyToOne
    @JoinColumn(name = "parentCategory_id")
    private Category parentCategory;
}
