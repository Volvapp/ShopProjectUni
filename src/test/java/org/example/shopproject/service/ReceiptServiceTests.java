package org.example.shopproject.service;

import org.example.shopproject.model.entity.Receipt;
import org.example.shopproject.repository.ReceiptRepository;
import org.example.shopproject.service.impl.ReceiptServiceImpl;
import org.example.shopproject.util.ValidationUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class ReceiptServiceTests {

    @Mock
    private ReceiptRepository receiptRepository;

    @Mock
    private ValidationUtil validationUtil;

    @InjectMocks
    private ReceiptServiceImpl receiptService;

    private Receipt receipt;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        receipt = new Receipt();
    }

    @Test
    void testAddReceiptSuccess() {
        when(validationUtil.isValid(receipt)).thenReturn(true);
        when(receiptRepository.findById(receipt.getId())).thenReturn(Optional.empty());

        String result = receiptService.addReceipt(receipt);

        assertEquals("Successfully added receipt!\n", result);
        verify(receiptRepository, times(1)).save(receipt);
    }

    @Test
    void testAddReceiptInvalid() {
        when(validationUtil.isValid(receipt)).thenReturn(false);

        String result = receiptService.addReceipt(receipt);

        assertEquals("Invalid receipt!\n", result);
        verify(receiptRepository, never()).save(receipt);
    }

    @Test
    void testAddReceiptAlreadyExists() {
        when(validationUtil.isValid(receipt)).thenReturn(true);
        when(receiptRepository.findById(receipt.getId())).thenReturn(Optional.of(receipt));

        String result = receiptService.addReceipt(receipt);

        assertEquals("Receipt already exists!\n", result);
        verify(receiptRepository, never()).save(receipt);
    }
}
