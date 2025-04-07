package com.example.expensetrackingsystem.repositories;

import com.example.expensetrackingsystem.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Integer> {


}
