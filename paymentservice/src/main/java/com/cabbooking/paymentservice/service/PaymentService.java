package com.cabbooking.paymentservice.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.cabbooking.paymentservice.dto.PaymentDto;
import com.cabbooking.paymentservice.entity.Payment;

@Service
public interface PaymentService {
	
    PaymentDto createPayment(PaymentDto paymentDto);   
    PaymentDto getPaymentById(Integer paymentId);
}
