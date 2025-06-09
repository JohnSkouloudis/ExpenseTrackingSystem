package com.example.expensetrackingsystem.controllers;

import com.example.expensetrackingsystem.dto.AccountDTO;
import com.example.expensetrackingsystem.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    // Endpoint to get transactions for a specific account
    @GetMapping("/{userId}")
    public ResponseEntity<List<AccountDTO>> getUserAccounts(@PathVariable int userId,@RequestHeader(value = "Authorization", required = false) String authHeader) {
        List<AccountDTO> accounts = accountService.getAccountsByUser(userId);
        return ResponseEntity.ok(accounts);
    }

    // Endpoint to get paginated transactions for a specific account
    @GetMapping("/{userId}/{page}")
    public ResponseEntity<Page<AccountDTO>> getPagedUserAccounts(@PathVariable int userId, @PathVariable int page, @RequestParam int size) {
        Page<AccountDTO> accounts = accountService.getAccountsByUser(userId, PageRequest.of(page, size));
        return ResponseEntity.ok(accounts);
    }

    // Endpoint to create a transaction for an account
    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<String> createAccount( @RequestBody AccountDTO accountdto) {
        accountService.saveAccount(accountdto);
        return ResponseEntity.ok("Account created successfully");
    }


    // TODO: implement update account

    @PutMapping("/update/{accountId}")
    public ResponseEntity<String> updateAccount(@PathVariable int accountId, @RequestBody AccountDTO accountdto) {
        accountService.updateAccount(accountId, accountdto);
        return ResponseEntity.ok("Account updated successfully");
    }


}
