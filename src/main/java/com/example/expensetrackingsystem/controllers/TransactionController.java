package com.example.expensetrackingsystem.controllers;

import com.example.expensetrackingsystem.dto.TransactionDTO;
import com.example.expensetrackingsystem.dto.TransactionDetails;
import com.example.expensetrackingsystem.entities.Transaction;
import com.example.expensetrackingsystem.services.ImageService;
import com.example.expensetrackingsystem.services.ReceiptService;
import com.example.expensetrackingsystem.services.TransactionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
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
    @PostMapping(value = "/add" , consumes = {MediaType.MULTIPART_FORM_DATA_VALUE} )
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<String> createTransaction(@RequestPart("data")String transactionDto,
                                                    @RequestPart(value = "image", required = false) MultipartFile image) throws JsonProcessingException {


        TransactionDTO dto = transactionService.convertToTransactionDto(transactionDto);

        Transaction transaction = transactionService.saveTransaction(dto);

        if (!image.isEmpty()) {
            receiptService.saveReceipt(transaction, image.getOriginalFilename());
            imageService.uploadImage("john", image);

        }
        return ResponseEntity.ok("Transaction created successfully");
    }




    @GetMapping("/details/{transactionId}")
    public ResponseEntity<TransactionDetails> getTransactionDetails(@PathVariable int transactionId) {

        TransactionDTO transactiondto = transactionService.getTransactionDetails(transactionId);
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



}
