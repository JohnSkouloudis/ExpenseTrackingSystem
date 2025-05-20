package com.example.expensetrackingsystem.services;


import com.example.expensetrackingsystem.entities.Receipt;
import com.example.expensetrackingsystem.entities.Transaction;
import com.example.expensetrackingsystem.repositories.ReceiptRepository;
import com.example.expensetrackingsystem.repositories.TransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReceiptService {


    private final ReceiptRepository receiptRepository;

    private final TransactionRepository transactionRepository;

    @Autowired
    public ReceiptService(ReceiptRepository receiptRepository,
                          TransactionRepository transactionRepository ) {
        this.receiptRepository = receiptRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public void saveReceipt(Transaction transaction,String imageName){

        if (transaction == null) {
            throw new IllegalArgumentException("Transaction cannot be null");
        }
        Receipt receipt = new Receipt();
        receipt.setTransaction(transaction);
        receipt.setImageName(imageName);

        receiptRepository.save(receipt);
    }

    @Transactional
    public String getImageName(int TransactionId) {
        Receipt receipt = receiptRepository.findByTransactionId(TransactionId);

        if (receipt != null) {
            return receipt.getImageName();
        } else {
            return null;
        }
    }


}
