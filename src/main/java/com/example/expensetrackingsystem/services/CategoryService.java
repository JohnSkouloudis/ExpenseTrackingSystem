package com.example.expensetrackingsystem.services;

import com.example.expensetrackingsystem.entities.Category;
import com.example.expensetrackingsystem.repositories.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }


    public List<Category> findAllCategories() {
        return categoryRepository.findAll();
    }

    public List<Category> findByParentCategory(String parentName) {

        Category parent = categoryRepository.findByCategoryName(parentName.toUpperCase())
                .orElseThrow(() -> new IllegalArgumentException("Parent category not found: " + parentName));

        List<Category> result = new ArrayList<>();
        collectSubcategories(parent, result);
        return result;
    }

    private void collectSubcategories(Category parent, List<Category> accumulator) {

        List<Category> children = categoryRepository.findByParentCategory(parent);

        for (Category child : children) {
            accumulator.add(child);

            collectSubcategories(child, accumulator);
        }
    }
}
