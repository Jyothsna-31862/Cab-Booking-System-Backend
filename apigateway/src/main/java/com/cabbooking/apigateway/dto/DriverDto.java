package com.cabbooking.apigateway.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverDto {
    private String id;
    private String name;
    private String email;
    private String phoneNumber;
    private String licenseNumber;
    private String vehicleDetails;
    private double rating;
    private boolean available;
}
