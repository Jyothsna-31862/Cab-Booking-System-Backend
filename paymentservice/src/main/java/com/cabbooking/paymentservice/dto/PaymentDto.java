package com.cabbooking.paymentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDto {

    private Integer paymentId;
    private Integer rideId;
    private Integer userId;
    private BigDecimal amount;
    private String method;
    private String status;
    private String cardNumber;
}
