package com.cabbooking.paymentservice.exception;

public class InvalidPaymentDataException extends RuntimeException {

    public InvalidPaymentDataException(String message) {
        super(message);
    }

    public InvalidPaymentDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
