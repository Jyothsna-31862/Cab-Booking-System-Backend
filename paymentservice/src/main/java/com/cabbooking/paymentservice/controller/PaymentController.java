package com.cabbooking.paymentservice.controller;

import java.util.Optional;

import com.cabbooking.paymentservice.dto.ApiResponse;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cabbooking.paymentservice.dto.PaymentDto;
import com.cabbooking.paymentservice.service.PaymentService;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

	private final PaymentService paymentService;

	public PaymentController(PaymentService paymentService)
	{
		this.paymentService = paymentService;
	}

	@PostMapping
	public ResponseEntity<ApiResponse> createPayment(@RequestBody PaymentDto paymentDto) {
		PaymentDto newPayment = paymentService.createPayment(paymentDto);
		ApiResponse apiResponse = new ApiResponse(true, "Payment created successfully", newPayment);
		return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
	}

	@GetMapping("/{paymentId}")
	public ResponseEntity<ApiResponse> getPaymentById(@PathVariable Integer paymentId) {
		PaymentDto payment = paymentService.getPaymentById(paymentId);
		ApiResponse apiResponse = new ApiResponse(true, "Payment retrieved successfully", payment);
		return new ResponseEntity<>(apiResponse, HttpStatus.OK);
	}

	@GetMapping("/receipt/{paymentId}")
		public ResponseEntity<ByteArrayResource> generateReceipt(@PathVariable Integer paymentId) {
		try {
			PaymentDto paymentDto = paymentService.getPaymentById(paymentId);

			byte[] pdfBytes = paymentService.generateReceiptPdf(paymentDto);

			ByteArrayResource resource = new ByteArrayResource(pdfBytes);

			return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=receipt_" + paymentId + ".pdf").contentType(MediaType.APPLICATION_PDF).contentLength(pdfBytes.length).body(resource);

		}
		catch(Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}

	}


}
