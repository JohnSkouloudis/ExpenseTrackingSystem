package com.example.expensetrackingsystem.components;


import com.example.expensetrackingsystem.dto.BudgetDTO;
import com.example.expensetrackingsystem.entities.Budget;
import com.example.expensetrackingsystem.entities.DurationEnum;
import com.example.expensetrackingsystem.services.BudgetService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class BudgetReset {

    @Autowired
    private BudgetService budgetService;


    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void ResetExpiredBudgets() {
        LocalDate now = LocalDate.of(2025, 6, 20);
        System.out.println("Resetting expired budgets at " + now);

        List<Budget> budgets = budgetService.getBudgetsByEndDate(now);

        if (budgets.isEmpty()) {
            System.out.println("No expired budgets found.");
            return;
        }
        System.out.println("Found " + budgets.size() + " expired budgets to reset.");

        for(Budget budget:budgets){

            int differenceInDays = budget.getStartDate().until(now).getDays();

            BudgetDTO dto = new BudgetDTO(
                    budget.getBudgetAmount(),
                    budget.getEndDate(),
                    differenceInDays,
                    DurationEnum.DAY
            );
            budgetService.updateBudget(dto, budget.getUser().getId());

        }

    }
}
