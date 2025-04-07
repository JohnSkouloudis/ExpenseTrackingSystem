package com.example.expensetrackingsystem.repositories;

import com.example.expensetrackingsystem.entities.Receipt;
import com.example.expensetrackingsystem.entities.Transaction;
import org.springframework.stereotype.Repository;

@Repository
public interface ReceiptRepository {

    Receipt findByTransaction(Transaction transaction);
}
