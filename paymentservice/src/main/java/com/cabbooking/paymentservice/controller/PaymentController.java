package com.cabbooking.paymentservice.controller;

import java.util.Optional;

import com.cabbooking.paymentservice.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.cabbooking.paymentservice.dto.PaymentDto;
import com.cabbooking.paymentservice.service.PaymentService;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

	private final PaymentService paymentService;

	public PaymentController(PaymentService paymentService) {
		this.paymentService = paymentService;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ApiResponse<PaymentDto> createPayment(@RequestBody PaymentDto paymentDto) {
		PaymentDto newPayment = paymentService.createPayment(paymentDto);
		return new ApiResponse<>(true, "Payment created successfully", newPayment);
	}

	@GetMapping("/{paymentId}")
	@ResponseStatus(HttpStatus.OK)
	public ApiResponse<PaymentDto> getPaymentById(@PathVariable Integer paymentId) {
		PaymentDto payment = paymentService.getPaymentById(paymentId);
		return new ApiResponse<>(true, "Payment retrieved successfully", payment);
	}

}
