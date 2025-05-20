package com.example.expensetrackingsystem.repositories;

import com.example.expensetrackingsystem.entities.Receipt;
import com.example.expensetrackingsystem.entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReceiptRepository extends JpaRepository<Receipt, Integer> {

    Receipt findByTransactionId(int transactionId);
}
