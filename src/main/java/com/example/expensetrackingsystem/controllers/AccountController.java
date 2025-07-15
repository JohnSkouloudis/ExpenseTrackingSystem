package com.example.expensetrackingsystem.controllers;

import com.example.expensetrackingsystem.dto.AccountDTO;
import com.example.expensetrackingsystem.services.AccountService;
import com.example.expensetrackingsystem.services.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private JwtService jwtService;

    // Endpoint to get transactions for a specific account
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserAccounts(@PathVariable int userId,@RequestHeader(value = "Authorization", required = false) String authHeader) {

        String token = authHeader.substring(7);

        int tokenUserId = jwtService.extractUserId(token);

        if(tokenUserId != userId) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not allowed to access this resource");
        }

        List<AccountDTO> accounts = accountService.getAccountsByUser(userId);
        return ResponseEntity.ok(accounts);
    }

    // Endpoint to get paginated transactions for a specific account
    @GetMapping("/{userId}/{page}")
    public ResponseEntity<?> getPagedUserAccounts(@PathVariable int userId,
                                                                 @PathVariable int page,
                                                                 @RequestParam int size,
                                                                 @RequestHeader (value = "Authorization",required =false) String authHeader) {

        String token = authHeader.substring(7);

        int tokenUserId = jwtService.extractUserId(token);

        if(tokenUserId != userId) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not allowed to access this resource");
        }

        Page<AccountDTO> accounts = accountService.getAccountsByUser(userId, PageRequest.of(page, size));
        return ResponseEntity.ok(accounts);
    }


    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<String> createAccount( @RequestBody AccountDTO accountdto,@RequestHeader (value = "Authorization",required =false) String authHeader) {

        String token = authHeader.substring(7);
        int userId = jwtService.extractUserId(token);

        accountdto.setUserId(userId);

        accountService.saveAccount(accountdto);
        return ResponseEntity.ok("Account created successfully");
    }

    @DeleteMapping("/delete/{accountId}")
    public ResponseEntity<String> deleteAccount(@PathVariable int accountId,@RequestHeader (value = "Authorization",required =false) String authHeader) {

        String token = authHeader.substring(7);
        int userId = jwtService.extractUserId(token);


        if (accountService.findByIdAndUser(accountId, userId) == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not allowed to delete this account");
        }

        accountService.deleteAccount(accountId);
        return ResponseEntity.ok("Account deleted successfully");
    }



    @PutMapping("/update")
    public ResponseEntity<String> updateAccount(@RequestBody AccountDTO accountdto,@RequestHeader (value = "Authorization",required =false) String authHeader) {
        String token = authHeader.substring(7);
        int userId = jwtService.extractUserId(token);
        if (accountService.findByIdAndUser(accountdto.getId(), userId) == null || accountdto.getUserId() != userId) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not allowed to update this account");
        }
        accountService.updateAccount(accountdto);
        return ResponseEntity.ok("Account updated successfully");
    }


}
