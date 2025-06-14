package com.example.expensetrackingsystem.repositories;

import com.example.expensetrackingsystem.entities.Budget;
import com.example.expensetrackingsystem.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Integer> {

    Budget findByUserId(int userId);
    List<Budget> findByEndDate(LocalDate endDate);
}
