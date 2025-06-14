package com.example.expensetrackingsystem.services;

import com.example.expensetrackingsystem.dto.BudgetDTO;
import com.example.expensetrackingsystem.entities.Budget;
import com.example.expensetrackingsystem.entities.DurationEnum;
import com.example.expensetrackingsystem.entities.User;
import com.example.expensetrackingsystem.repositories.BudgetRepository;
import com.example.expensetrackingsystem.repositories.UserRepository;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class BudgetService {

    private final BudgetRepository budgetRepository;

    private final UserRepository userRepository;

    @Autowired
    public BudgetService(BudgetRepository budgetRepository,
                         UserRepository userRepository)  {
        this.budgetRepository = budgetRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void updateBudgetRemainingAmount(Budget budget, float newRemainingAmount){

        budget.setRemainingAmount(newRemainingAmount);
        budgetRepository.save(budget);
    }

    @Transactional
    public void createBudget(BudgetDTO dto , int userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Budget budget = new Budget();
        budget.setUser(user);
        budget.setBudgetAmount(dto.budgetAmount());
        budget.setRemainingAmount(dto.budgetAmount());
        budget.setStartDate(dto.startDate());

        switch (dto.durationUnit()) {
            case DAY -> budget.setEndDate( budget.getStartDate().plusDays(dto.duration() ) );
            case WEEK -> budget.setEndDate( budget.getStartDate().plusWeeks(dto.duration() ) );
            case MONTH -> budget.setEndDate( budget.getStartDate().plusMonths(dto.duration() ) );
            case YEAR -> budget.setEndDate( budget.getStartDate().plusYears(dto.duration() ) );
            default -> throw new IllegalArgumentException("Invalid duration unit");
        }

         budgetRepository.save(budget);
    }

    @Transactional
    public void updateBudget(BudgetDTO dto, int userId) {

        Budget budget = budgetRepository.findByUserId(userId);
        if (budget == null) {
            throw new IllegalArgumentException("Budget not found for user with ID: " + userId);
        }

        budget.setBudgetAmount(dto.budgetAmount());
        budget.setRemainingAmount(dto.budgetAmount());
        budget.setStartDate(dto.startDate());

        switch (dto.durationUnit()) {
            case DAY -> budget.setEndDate( budget.getStartDate().plusDays(dto.duration() ) );
            case WEEK -> budget.setEndDate( budget.getStartDate().plusWeeks(dto.duration() ) );
            case MONTH -> budget.setEndDate( budget.getStartDate().plusMonths(dto.duration() ) );
            case YEAR -> budget.setEndDate( budget.getStartDate().plusYears(dto.duration() ) );
            default -> throw new IllegalArgumentException("Invalid duration unit");
        }

        budgetRepository.save(budget);
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

    public List<Budget> getBudgetsByEndDate(LocalDate endDate) {

        if (endDate == null) {
            throw new IllegalArgumentException("End date cannot be null");
        }
        return budgetRepository.findByEndDate(endDate);
    }

}
