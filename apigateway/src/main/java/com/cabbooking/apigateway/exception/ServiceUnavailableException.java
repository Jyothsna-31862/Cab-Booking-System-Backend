package com.cabbooking.apigateway.exception;

import lombok.Getter;

@Getter
public class ServiceUnavailableException extends RuntimeException {
    private final String serviceName;

    public ServiceUnavailableException(String serviceName) {
        super(serviceName + " service is currently unavailable.");
        this.serviceName = serviceName;
    }
}

