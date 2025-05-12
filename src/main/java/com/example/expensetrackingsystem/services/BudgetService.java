package com.example.expensetrackingsystem.services;

import com.example.expensetrackingsystem.entities.Budget;
import com.example.expensetrackingsystem.repositories.BudgetRepository;
import jakarta.persistence.Access;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BudgetService {

    private final BudgetRepository budgetRepository;

    @Autowired
    public BudgetService(BudgetRepository budgetRepository) {
        this.budgetRepository = budgetRepository;
    }

    @Transactional
    public Budget createBudget(Budget budget) {
        return budgetRepository.save(budget);
    }

    @Transactional
    public Budget getBudgetByUserId(int userId) {
        return budgetRepository.findByUserId(userId);
    }

    @Transactional
    public void deleteBudgetByUserId(int userId) {
        Budget budget = budgetRepository.findByUserId(userId);
        if (budget != null) {
            budgetRepository.delete(budget);
        }
    }

}
