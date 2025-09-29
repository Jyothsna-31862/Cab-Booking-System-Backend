package com.cabbooking.paymentservice.controller;

import java.util.Map;

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

	private static final String SUCCESS_STATUS = "success";
	private static final String ERROR_STATUS = "error";

	private final PaymentService paymentService;

	public PaymentController(PaymentService paymentService)
	{
		this.paymentService = paymentService;
	}

	@PostMapping
	public ResponseEntity<ApiResponse> createPayment(@RequestBody PaymentDto paymentDto) {
		PaymentDto newPayment = paymentService.createPayment(paymentDto);
		ApiResponse apiResponse = new ApiResponse(SUCCESS_STATUS, "Payment created successfully", newPayment);
		return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
	}

	@GetMapping("/{paymentId}")
	public ResponseEntity<ApiResponse> getPaymentById(@PathVariable String paymentId) {
		PaymentDto payment = paymentService.getPaymentById(paymentId);
		ApiResponse apiResponse = new ApiResponse(SUCCESS_STATUS, "Payment retrieved successfully", payment);
		return new ResponseEntity<>(apiResponse, HttpStatus.OK);
	}

	@GetMapping("/receipt/{paymentId}")
		public ResponseEntity<ByteArrayResource> generateReceipt(@PathVariable String paymentId) {
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


	@PatchMapping("/{rideId}/status")
	public ResponseEntity<ApiResponse> updatePaymentStatus(
			@PathVariable String rideId,
			@RequestBody Map<String, String> statusUpdate) {

		try {
			String newStatus = statusUpdate.get("status");
			if (newStatus == null || newStatus.trim().isEmpty()) {
				ApiResponse errorResponse = new ApiResponse(ERROR_STATUS, "Status is required", null);
				return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
			}

			PaymentDto updatedPayment = paymentService.updatePaymentStatus(rideId, newStatus);
			ApiResponse apiResponse = new ApiResponse(SUCCESS_STATUS,
					"Payment status updated successfully to: " + newStatus, updatedPayment);

			return new ResponseEntity<>(apiResponse, HttpStatus.OK);

		} catch (IllegalArgumentException e) {
			ApiResponse errorResponse = new ApiResponse(ERROR_STATUS, e.getMessage(), null);
			return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);

		} catch (Exception e) {
			ApiResponse errorResponse = new ApiResponse(ERROR_STATUS, "Failed to update payment status", null);
			return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
