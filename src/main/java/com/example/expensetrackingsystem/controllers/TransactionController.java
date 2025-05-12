package com.example.expensetrackingsystem.controllers;

import com.example.expensetrackingsystem.entities.Transaction;
import com.example.expensetrackingsystem.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    // Endpoint to get transactions for a specific account
    @GetMapping("/{accountId}")
    public ResponseEntity<List<Transaction>> getAccountTransactions(@PathVariable int accountId) {
        List<Transaction> transactions = transactionService.getAccountTransactions(accountId);
        return ResponseEntity.ok(transactions);
    }

    // Endpoint to get paginated transactions for a specific account
    @GetMapping("/{accountId}/{page}")
    public ResponseEntity<Page<Transaction>> getPagedAccountTransactions(@PathVariable int accountId, @PathVariable int page, @RequestParam int size) {
        Page<Transaction> transactions = transactionService.getAccountTransactions(accountId, PageRequest.of(page, size));
        return ResponseEntity.ok(transactions);
    }

    // Endpoint to create a transaction for an account
    @PostMapping("/{accountId}")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<String> createTransaction(@PathVariable int accountId, @RequestBody Transaction transaction) {
        transactionService.saveTransaction(transaction);
        return ResponseEntity.ok("Transaction created successfully");
    }

}
