package com.cabbooking.paymentservice.controller;

import java.util.Map;
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

@CrossOrigin("http://localhost:4200/")
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
		ApiResponse apiResponse = new ApiResponse("success", "Payment created successfully", newPayment);
		return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
	}

	@GetMapping("/{paymentId}")
	public ResponseEntity<ApiResponse> getPaymentById(@PathVariable String paymentId) {
		PaymentDto payment = paymentService.getPaymentById(paymentId);
		ApiResponse apiResponse = new ApiResponse("success", "Payment retrieved successfully", payment);
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

	/**
	 * API to update payment status
	 * PUT /api/payments/{paymentId}/status
	 */
	@PutMapping("/{paymentId}/status")
	public ResponseEntity<ApiResponse> updatePaymentStatus(
			@PathVariable String paymentId,
			@RequestBody Map<String, String> statusUpdate) {

		try {
			String newStatus = statusUpdate.get("status");
			if (newStatus == null || newStatus.trim().isEmpty()) {
				ApiResponse errorResponse = new ApiResponse("error", "Status is required", null);
				return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
			}

			PaymentDto updatedPayment = paymentService.updatePaymentStatus(paymentId, newStatus);
			ApiResponse apiResponse = new ApiResponse("success",
					"Payment status updated successfully to: " + newStatus, updatedPayment);

			return new ResponseEntity<>(apiResponse, HttpStatus.OK);

		} catch (IllegalArgumentException e) {
			ApiResponse errorResponse = new ApiResponse("error", e.getMessage(), null);
			return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);

		} catch (Exception e) {
			ApiResponse errorResponse = new ApiResponse("error", "Failed to update payment status", null);
			return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Alternative API to update payment status using query parameter
	 * PATCH /api/payments/{paymentId}/status?status={newStatus}
	 */
	@PatchMapping("/{paymentId}/status")
	public ResponseEntity<ApiResponse> updatePaymentStatusWithParam(
			@PathVariable String paymentId,
			@RequestParam String status) {

		try {
			PaymentDto updatedPayment = paymentService.updatePaymentStatus(paymentId, status);
			ApiResponse apiResponse = new ApiResponse("success",
					"Payment status updated successfully to: " + status, updatedPayment);

			return new ResponseEntity<>(apiResponse, HttpStatus.OK);

		} catch (IllegalArgumentException e) {
			ApiResponse errorResponse = new ApiResponse("error", e.getMessage(), null);
			return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);

		} catch (Exception e) {
			ApiResponse errorResponse = new ApiResponse("error", "Failed to update payment status", null);
			return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
