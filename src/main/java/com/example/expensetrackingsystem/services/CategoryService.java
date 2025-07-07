package com.example.expensetrackingsystem.services;

import com.example.expensetrackingsystem.dto.CategoryDTO;
import com.example.expensetrackingsystem.dto.CategorySummaryDTO;
import com.example.expensetrackingsystem.entities.Account;
import com.example.expensetrackingsystem.entities.Category;
import com.example.expensetrackingsystem.repositories.AccountRepository;
import com.example.expensetrackingsystem.repositories.CategoryRepository;
import com.example.expensetrackingsystem.repositories.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    private final AccountRepository accountRepository;

    private final TransactionRepository transactionRepository;

    public CategoryService(CategoryRepository categoryRepository ,
                           AccountRepository accountRepository, TransactionRepository transactionRepository)  {
        this.categoryRepository = categoryRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }


    public List<Category> findAllCategories() {
        return categoryRepository.findAll();
    }

    public  List<Category> findByParentCategory(String parentName) {

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

    public List<CategorySummaryDTO> getCategorySummaries(int userId) {
        List<Category> topCategories = categoryRepository.findByParentCategory(null);
        List<CategorySummaryDTO> summaries = new ArrayList<>();
        List<Account> accounts = accountRepository.findByUserId(userId);

        for (Category parent : topCategories) {

            List<Category> subcategories = findByParentCategory(parent.getCategoryName());
            subcategories.add(parent);

            List<CategoryDTO> subcategoryDTOs = new ArrayList<>();
            long subcategoriesTotalCount = 0;

            for (Category sub : subcategories) {
                long subCount = transactionRepository.countByCategoryAndAccountIn(sub, accounts);
                subcategoriesTotalCount += subCount;
                if(subCount>0) {
                    subcategoryDTOs.add(new CategoryDTO(sub.getCategoryName(), subCount));
                }
            }



            CategorySummaryDTO summary = new CategorySummaryDTO();
            summary.setType(parent.getCategoryName());
            summary.setCount(subcategoriesTotalCount);
            summary.setSubcategories(subcategoryDTOs);

            summaries.add(summary);
        }

        return summaries;
    }

}
