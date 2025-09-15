package com.cabbooking.dto;

import lombok.Data;

@Data
public class DriverDto {
    private String driverId;
    private String fullName;
    private String email;
    private String phone;
    private String licenceNumber;
    private String gender;
    private String vehicleNumber;
    private String vehicleName;
    private String carSeater;  // Changed from int to String to match Driver entity
    private double rating;     // Added for rating
   // Added for status
    private boolean isAvailable; // Added for availability check
  // Added for password reset functionality
}
