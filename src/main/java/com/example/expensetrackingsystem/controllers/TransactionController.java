package com.example.expensetrackingsystem.controllers;

import com.example.expensetrackingsystem.dto.AccountDTO;
import com.example.expensetrackingsystem.dto.TransactionDTO;
import com.example.expensetrackingsystem.dto.TransactionDetails;
import com.example.expensetrackingsystem.entities.Account;
import com.example.expensetrackingsystem.entities.Transaction;
import com.example.expensetrackingsystem.services.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;


@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private ImageService imageService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private ReceiptService receiptService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AccountService accountService;

    // Endpoint to get transactions for a specific account
    @GetMapping("/{accountId}")
    public ResponseEntity<?> getAccountTransactions(@PathVariable int accountId,@RequestHeader(value = "Authorization",required = false) String authHeader) {

        String token = authHeader.substring(7);

        int userId = jwtService.extractUserId(token);

        if( accountService.findByIdAndUser( accountId,userId) == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not allowed to access this resource");
        }


        List<TransactionDTO> transactions = transactionService.getAccountTransactions(accountId);
        return ResponseEntity.ok(transactions);
    }

    // Endpoint to get paginated transactions for a specific account
    @GetMapping("/{accountId}/{page}")
    public ResponseEntity<?> getPagedAccountTransactions(@PathVariable int accountId,
                                                                            @PathVariable int page,
                                                                            @RequestParam int size,
                                                                            @RequestHeader(value = "Authorization",required = false) String authHeader) {
        String token = authHeader.substring(7);

        int userId = jwtService.extractUserId(token);

        if( accountService.findByIdAndUser( accountId,userId) == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not allowed to access this resource");
        }

        Page<TransactionDTO> transactions = transactionService.getAccountTransactions(accountId, PageRequest.of(page, size));
        return ResponseEntity.ok(transactions);
    }



    // Endpoint to create a transaction for an account
    @PostMapping(value = "/add" , consumes = {MediaType.MULTIPART_FORM_DATA_VALUE} )
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<String> createTransaction(@RequestPart("data")String transactionDto,
                                                    @RequestPart(value = "image", required = false) MultipartFile image,
                                                    @RequestHeader(value = "Authorization",required = false) String authHeader) throws JsonProcessingException {

        String token = authHeader.substring(7);

        if(!jwtService.isTokenValid(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }

       String username = jwtService.extractUsername(token);


        TransactionDTO dto = transactionService.convertToTransactionDto(transactionDto);

        if(accountService.findByIdAndUser(dto.accountId(),jwtService.extractUserId(token)) == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not allowed to access this resource");
        }

        Transaction transaction = transactionService.saveTransaction(dto);

        if (image != null) {
            receiptService.saveReceipt(transaction, image.getOriginalFilename());
            imageService.uploadImage(username, image);

        }
        return ResponseEntity.ok("Transaction created successfully");
    }




    @GetMapping("/details/{transactionId}")
    public ResponseEntity<?> getTransactionDetails(@PathVariable int transactionId,
                                                                    @RequestHeader (value = "Authorization",required =false) String authHeader ) {

        String token = authHeader.substring(7);

        if(!jwtService.isTokenValid(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }

        int userId = jwtService.extractUserId(token);

        TransactionDTO transactiondto = transactionService.getTransactionDetails(transactionId);

        if( accountService.findByIdAndUser(transactiondto.accountId(),userId) == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not allowed to access this resource");
        }

        String imageName = receiptService.getReceiptImageName(transactionId);
        String base64Image = null;

        if (imageName != null) {

            try {
                InputStream imageStream = imageService.getImage("john", imageName);
                byte[] imageData = imageStream.readAllBytes();
                base64Image = Base64.getEncoder().encodeToString(imageData);

            } catch (Exception e) {
                throw new RuntimeException("Error reading image: " + e.getMessage(), e);
            }


        }
        TransactionDetails  response= new TransactionDetails(transactiondto, base64Image);

        return ResponseEntity.ok(response);
    }

    @GetMapping("exportToCsv")
    public void ExportTransactionsToCsv(HttpServletResponse response ,
                                        @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                        @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate ,
                                        @RequestHeader(value = "Authorization",required =false) String authHeader) throws CsvDataTypeMismatchException, CsvRequiredFieldEmptyException, IOException {

        String token = authHeader.substring(7);
        int userId = jwtService.extractUserId(token);

        List<AccountDTO> accounts = accountService.getAccountsByUser(userId);


        String csvFileName = "transactions.csv";
        response.setContentType("text/csv");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + csvFileName + "");

        StatefulBeanToCsv<TransactionDTO> writer = new StatefulBeanToCsvBuilder<TransactionDTO>(response.getWriter())
                .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
                .withOrderedResults(true)
                .build();

        writer.write(transactionService.findByDateBetween(startDate, endDate, accounts));

    }




}
