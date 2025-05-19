package com.example.expensetrackingsystem.controllers;

import com.example.expensetrackingsystem.dto.TransactionDTO;
import com.example.expensetrackingsystem.services.ImageService;
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
    private ImageService imageService;

    @Autowired
    private TransactionService transactionService;

    // Endpoint to get transactions for a specific account
    @GetMapping("/{accountId}")
    public ResponseEntity<List<TransactionDTO>> getAccountTransactions(@PathVariable int accountId) {
        List<TransactionDTO> transactions = transactionService.getAccountTransactions(accountId);
        return ResponseEntity.ok(transactions);
    }

    // Endpoint to get paginated transactions for a specific account
    @GetMapping("/{accountId}/{page}")
    public ResponseEntity<Page<TransactionDTO>> getPagedAccountTransactions(@PathVariable int accountId, @PathVariable int page, @RequestParam int size) {
        Page<TransactionDTO> transactions = transactionService.getAccountTransactions(accountId, PageRequest.of(page, size));
        return ResponseEntity.ok(transactions);
    }

    // Endpoint to create a transaction for an account
    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<String> createTransaction( @RequestBody TransactionDTO transactiondto) {
        transactionService.saveTransaction(transactiondto);
        return ResponseEntity.ok("Transaction created successfully");
    }


    @PostMapping("/create-bucket")
    public ResponseEntity<String> createBucket(@RequestParam String name) {
        try {
            imageService.createBucket(name);
            return ResponseEntity.ok("Bucket created: " + name);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

}
