package com.example.expensetrackingsystem.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "categories")
public class Category {

    @Id
    private int id;

    private String categoryName;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "parentCategory_id")
    private Category parentCategory;


}
