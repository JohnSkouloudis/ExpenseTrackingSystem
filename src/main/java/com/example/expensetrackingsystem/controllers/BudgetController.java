package com.example.expensetrackingsystem.controllers;

import com.example.expensetrackingsystem.dto.BudgetDTO;
import com.example.expensetrackingsystem.services.BudgetService;
import com.example.expensetrackingsystem.services.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/budget")
public class BudgetController {

    @Autowired
    private BudgetService budgetService;

    @Autowired
    private JwtService jwtService;


    @PostMapping("/create")
    public ResponseEntity<String> createBudget(@RequestBody BudgetDTO budget, @RequestHeader (value = "Authorization",required =false) String header) {

        String token = header.substring(7);
        int userId = jwtService.extractUserId(token);

        if(budgetService.getBudgetByUserId(userId) != null) {

            return ResponseEntity.status(HttpStatus.CONFLICT).body("Budget already exists for this user");
        }
        budgetService.createBudget(budget, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body("Budget created successfully");

    }

    @PutMapping("/update")
    public ResponseEntity<String> updateBudget(@RequestBody BudgetDTO budget, @RequestHeader (value = "Authorization",required =false) String header) {

        String token = header.substring(7);
        int userId = jwtService.extractUserId(token);

        if(budgetService.getBudgetByUserId(userId) == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Budget not found for this user");
        }
        budgetService.updateBudget(budget, userId);
        return ResponseEntity.ok("Budget updated successfully");


    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteBudget(@RequestHeader(value = "Authorization",required =false) String header) {

        String token = header.substring(7);
        int userId = jwtService.extractUserId(token);

        if(budgetService.getBudgetByUserId(userId) == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Budget not found for this user");
        }
        budgetService.deleteBudgetByUserId(userId);
        return ResponseEntity.ok("Budget deleted successfully");

    }

}
