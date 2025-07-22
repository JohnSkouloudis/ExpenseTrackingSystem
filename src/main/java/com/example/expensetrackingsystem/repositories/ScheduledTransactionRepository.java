package com.example.expensetrackingsystem.repositories;


import com.example.expensetrackingsystem.entities.ScheduledTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduledTransactionRepository extends JpaRepository<ScheduledTransaction, Integer> {

 Optional<List<ScheduledTransaction>> findByUserId(int userId);
 List<ScheduledTransaction> findByExecutionDate(LocalDate executionDate);
 Optional<ScheduledTransaction> findByIdAndUserId(int id, int userId);
 int countByUserId(int userId);
}
