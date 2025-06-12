package com.example.expensetrackingsystem.components;

import com.example.expensetrackingsystem.entities.Budget;
import com.example.expensetrackingsystem.services.BudgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import com.example.expensetrackingsystem.entities.CategoryEnum;

@Component
public class BudgetListener {

    @Autowired
    private BudgetService budgetService;

    @EventListener
    public void UpdateBudgetRemainingAmount(TransactionCreationEvent event) {
        int userId = event.getUserId();
        int categoryId = event.getTransaction().getCategory().getId();

        Budget budget = budgetService.getBudgetByUserId(userId);

        if( budget != null &&  CategoryEnum.isCategoryExpense(categoryId) ) {

            budgetService.updateBudgetRemainingAmount( budget, event.getTransaction().getAmount());

        }

    }
}
