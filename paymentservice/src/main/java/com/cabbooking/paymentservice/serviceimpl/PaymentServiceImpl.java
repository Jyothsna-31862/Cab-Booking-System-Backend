package com.cabbooking.paymentservice.serviceimpl;

import java.util.Optional;

import com.cabbooking.paymentservice.exception.PaymentFailedException;
import com.cabbooking.paymentservice.exception.PaymentNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cabbooking.paymentservice.dto.PaymentDto;
import com.cabbooking.paymentservice.entity.Payment;
import com.cabbooking.paymentservice.repository.PaymentRepository;
import com.cabbooking.paymentservice.service.PaymentService;

@Service
public class PaymentServiceImpl implements PaymentService {

	private final PaymentRepository paymentRepository;

	private final ModelMapper modelMapper;

	public PaymentServiceImpl(PaymentRepository paymentRepository, ModelMapper modelMapper) {
		this.paymentRepository = paymentRepository;
		this.modelMapper = modelMapper;
	}

	@Override
	public PaymentDto createPayment(PaymentDto paymentDto) {

		if ("failed".equalsIgnoreCase(paymentDto.getStatus())) {
			throw new PaymentFailedException("Payment with status 'failed' cannot be processed.");
		}

		Payment payment = modelMapper.map(paymentDto, Payment.class);

		Payment savedPayment = paymentRepository.save(payment);

		return modelMapper.map(savedPayment, PaymentDto.class);
	}

	@Override
	public PaymentDto getPaymentById(Integer paymentId) {
		return paymentRepository.findById(paymentId)
				.map(payment -> modelMapper.map(payment, PaymentDto.class))
				.orElseThrow(() -> new PaymentNotFoundException("Payment with ID " + paymentId + " not found."));
	}





}
