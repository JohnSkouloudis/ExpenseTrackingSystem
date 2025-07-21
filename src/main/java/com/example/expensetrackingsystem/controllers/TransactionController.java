package com.example.expensetrackingsystem.controllers;

import com.example.expensetrackingsystem.dto.AccountDTO;
import com.example.expensetrackingsystem.dto.TransactionDTO;
import com.example.expensetrackingsystem.dto.TransactionDetails;
import com.example.expensetrackingsystem.dto.CategorySummaryDTO;
import com.example.expensetrackingsystem.entities.ScheduledTransaction;
import com.example.expensetrackingsystem.entities.Transaction;
import com.example.expensetrackingsystem.entities.User;
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
import org.springframework.data.domain.Sort;
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

    @Autowired
    private CategoryService categoryService;

    // Endpoint to get transactions for a specific account
    @GetMapping("/{accountId}")
    public ResponseEntity<?> getAccountTransactions(@PathVariable int accountId,@RequestHeader(value = "Authorization",required = false) String authHeader,
                                                    @RequestParam(value = "startDate",required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                    @RequestParam(value = "endDate",required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        String token = authHeader.substring(7);

        int userId = jwtService.extractUserId(token);

        if( accountService.findByIdAndUser( accountId,userId) == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not allowed to access this resource");
        }

        if (startDate!= null && endDate != null) {
            AccountDTO account = accountService.getAccountById(accountId);
            List<AccountDTO> accounts = List.of(account);
            List<TransactionDTO> transactions = transactionService.findByDateBetween(startDate, endDate,accounts );
            return ResponseEntity.ok(transactions);
        }


        List<TransactionDTO> transactions = transactionService.getAccountTransactions(accountId);
        return ResponseEntity.ok(transactions);
    }

    // Endpoint to get paginated transactions for a specific account
    @GetMapping("/{accountId}/{page}")
    public ResponseEntity<?> getPagedAccountTransactions(@PathVariable int accountId,
                                                                            @PathVariable int page,
                                                                            @RequestParam int size,
                                                                            @RequestParam(required = false,defaultValue = "false")  boolean sortByDate,
                                                                            @RequestHeader(value = "Authorization",required = false) String authHeader) {
        String token = authHeader.substring(7);

        int userId = jwtService.extractUserId(token);

        if( accountService.findByIdAndUser( accountId,userId) == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not allowed to access this resource");
        }

        Page<TransactionDTO> transactions;

        if(sortByDate){
             transactions = transactionService.getAccountTransactions(accountId, PageRequest.of(page, size, Sort.by("date").descending()));
        }else {
             transactions = transactionService.getAccountTransactions(accountId, PageRequest.of(page, size));
        }
        return ResponseEntity.ok(transactions);
    }




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
        String username = jwtService.extractUsername(token);
        TransactionDTO transactiondto = transactionService.getTransactionDetails(transactionId);

        if( accountService.findByIdAndUser(transactiondto.accountId(),userId) == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not allowed to access this resource");
        }

        String imageName = receiptService.getReceiptImageName(transactionId);
        String base64Image = null;

        if (imageName != null) {

            try {
                InputStream imageStream = imageService.getImage(username, imageName);
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
    @GetMapping("/summary")
    public List<CategorySummaryDTO> getTransactionSummary(@RequestHeader(value = "Authorization",required =false) String authHeader){
        String token = authHeader.substring(7);
        int userId = jwtService.extractUserId(token);

        if(!jwtService.isTokenValid(token)) {
            throw new IllegalArgumentException("Invalid token");
        }

        return categoryService.getCategorySummaries(userId);


    }

    // Endpoint to get all scheduled transactions for a user

    @GetMapping("/scheduledTransactions/all")
    public ResponseEntity<?> getScheduledTransactions(@RequestHeader(value = "Authorization",required =false) String authHeader){
        String token = authHeader.substring(7);
        int userId = jwtService.extractUserId(token);

        if(!jwtService.isTokenValid(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }

        List<ScheduledTransaction> scheduledTransactions = transactionService.getScheduledTransactions(userId);
        return ResponseEntity.ok(scheduledTransactions);
    }

    @PostMapping("/scheduledTransactions/add")
    public ResponseEntity<?> addScheduledTransaction(@RequestBody ScheduledTransaction scheduledTransaction ,
                                                     @RequestHeader(value = "Authorization",required =false) String authHeader ){
        String token = authHeader.substring(7);
        int userId = jwtService.extractUserId(token);

        if(!jwtService.isTokenValid(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }

        if (accountService.findByIdAndUser(scheduledTransaction.getAccountId(), userId) == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not allowed to access this resource");
        }


        transactionService.saveScheduledTransaction(scheduledTransaction,userId);
        return ResponseEntity.ok("Scheduled transaction added successfully");
    }

    @DeleteMapping("/scheduledTransactions/delete/{scheduledTransactionId}")
    public ResponseEntity<?> deleteScheduledTransactions(@PathVariable int scheduledTransactionId ,
                                                      @RequestHeader(value = "Authorization",required =false) String authHeader){
        String token = authHeader.substring(7);
        int userId = jwtService.extractUserId(token);

        if(!jwtService.isTokenValid(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }

        if( transactionService.getScheduledTransactionByIdAndUserId(scheduledTransactionId, userId) == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not allowed to access this resource");
        }
        transactionService.deleteScheduledTransaction(scheduledTransactionId);

        return ResponseEntity.ok("Scheduled transaction deleted successfully");
    }





}
