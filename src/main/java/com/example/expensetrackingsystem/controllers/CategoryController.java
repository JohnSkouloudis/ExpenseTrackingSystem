package com.example.expensetrackingsystem.controllers;

import com.example.expensetrackingsystem.entities.Category;
import com.example.expensetrackingsystem.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/all")
    public List<Category> getAllCategories() {

        return categoryService.findAllCategories();
    }

    @GetMapping("/{parentName}")
    public List<Category> getSubCategories(@PathVariable String parentName) {

        return categoryService.findByParentCategory(parentName);
    }
}
