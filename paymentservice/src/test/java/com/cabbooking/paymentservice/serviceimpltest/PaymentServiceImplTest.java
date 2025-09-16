package com.cabbooking.paymentservice.serviceimpltest;

import com.cabbooking.paymentservice.dto.PaymentDto;
import com.cabbooking.paymentservice.entity.Payment;
import com.cabbooking.paymentservice.exception.PaymentFailedException;
import com.cabbooking.paymentservice.exception.PaymentNotFoundException;
import com.cabbooking.paymentservice.repository.PaymentRepository;
import com.cabbooking.paymentservice.service.PaymentService;
import com.cabbooking.paymentservice.serviceimpl.PaymentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private PaymentDto paymentDto;
    private Payment payment;

    @BeforeEach
    void setUp() {
        paymentDto = new PaymentDto();
        paymentDto.setPaymentId(1); // Added this line
        paymentDto.setRideId(1);
        paymentDto.setUserId(101);
        paymentDto.setAmount(new BigDecimal("50.00"));
        paymentDto.setMethod("Credit Card");
        paymentDto.setStatus("completed");

        payment = new Payment();
        payment.setPaymentId(1);
        payment.setRideId(1);
        payment.setUserId(101);
        payment.setAmount(new BigDecimal("50.00"));
        payment.setMethod("Credit Card");
        payment.setStatus("completed");
    }

    @Test
    void createPayment_Success() {
        // Mocking the behavior of ModelMapper and PaymentRepository
        when(modelMapper.map(paymentDto, Payment.class)).thenReturn(payment);
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);
        when(modelMapper.map(payment, PaymentDto.class)).thenReturn(paymentDto);

        // Calling the service method
        PaymentDto result = paymentService.createPayment(paymentDto);

        // Asserting the results
        assertNotNull(result);
        assertEquals(paymentDto.getUserId(), result.getUserId());
        assertEquals("completed", result.getStatus());
    }

    @Test
    void createPayment_FailedStatus() {
        // Setting the status to "failed"
        paymentDto.setStatus("failed");

        // Asserting that an exception is thrown
        assertThrows(PaymentFailedException.class, () -> paymentService.createPayment(paymentDto));
    }

    @Test
    void createPayment_RepositoryThrowsException() {
        // Mock the repository to throw a RuntimeException
        when(modelMapper.map(paymentDto, Payment.class)).thenReturn(payment);
        when(paymentRepository.save(any(Payment.class))).thenThrow(new RuntimeException("Database error"));

        // Assert that the RuntimeException is caught and re-thrown
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> paymentService.createPayment(paymentDto));
        assertEquals("Payment saving failed unexpectedly.", thrown.getMessage());
    }

    @Test
    void getPaymentById_Success() {
        // Mocking the behavior of PaymentRepository and ModelMapper
        when(paymentRepository.findById(1)).thenReturn(Optional.of(payment));
        when(modelMapper.map(payment, PaymentDto.class)).thenReturn(paymentDto);

        // Calling the service method
        PaymentDto result = paymentService.getPaymentById(1);

        // Asserting the results
        assertNotNull(result);
        assertEquals(payment.getPaymentId(), result.getPaymentId());
    }

    @Test
    void getPaymentById_NotFound() {
        // Mocking the behavior for a non-existent payment
        when(paymentRepository.findById(2)).thenReturn(Optional.empty());

        // Asserting that an exception is thrown
        assertThrows(PaymentNotFoundException.class, () -> paymentService.getPaymentById(2));
    }
}
