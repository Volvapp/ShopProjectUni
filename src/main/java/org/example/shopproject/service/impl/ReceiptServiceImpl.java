package org.example.shopproject.service.impl;

import org.example.shopproject.model.entity.Receipt;
import org.example.shopproject.repository.ReceiptRepository;
import org.example.shopproject.util.ValidationUtil;
import org.springframework.stereotype.Service;
import org.example.shopproject.service.ReceiptService;

import java.util.Optional;

@Service
public class ReceiptServiceImpl implements ReceiptService {
    private final ReceiptRepository receiptRepository;
    private final ValidationUtil validationUtil;

    public ReceiptServiceImpl(ReceiptRepository receiptRepository, ValidationUtil validationUtil) {
        this.receiptRepository = receiptRepository;
        this.validationUtil = validationUtil;
    }

    @Override
    public String addReceipt(Receipt receipt) {
        if (!validationUtil.isValid(receipt)) {
            return "Invalid receipt!\n";
        }
        Optional<Receipt> optionalReceipt = receiptRepository.findById(receipt.getId());
        if (optionalReceipt.isPresent()){
            return "Receipt already exists!\n";
        }
        this.receiptRepository.save(receipt);
        return "Successfully added receipt!\n";
    }
}
