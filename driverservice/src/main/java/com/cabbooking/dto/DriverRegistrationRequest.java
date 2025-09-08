package com.cabbooking.dto;

import lombok.Data;

@Data
public class DriverRegistrationRequest {
    private String fullName;
    private String email;
    private String phone;
    private String password;
    private String licenceNumber;
    private String gender;
    private String vehicleNumber;
    private String vehicleName;
    private int carSeater;
}
