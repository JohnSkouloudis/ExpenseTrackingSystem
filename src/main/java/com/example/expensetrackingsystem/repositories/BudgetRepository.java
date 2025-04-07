package com.example.expensetrackingsystem.repositories;

import com.example.expensetrackingsystem.entities.Budget;
import com.example.expensetrackingsystem.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Integer> {

    Budget findByUser(User user);
}
