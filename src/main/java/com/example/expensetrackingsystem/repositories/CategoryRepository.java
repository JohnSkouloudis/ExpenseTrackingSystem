package com.example.expensetrackingsystem.repositories;

import com.example.expensetrackingsystem.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

    Optional<Category> findByCategoryName(String name);

    List<Category> findByParentCategory(Category parentCategory);
}
