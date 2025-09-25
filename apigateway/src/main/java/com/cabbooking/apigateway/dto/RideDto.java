package com.cabbooking.apigateway.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RideDto {
    private String id;
    private String userId;
    private String driverId;
    private String pickupLocation;
    private String destination;
    private String rideStatus;
    private double fare;
    private String rideType;
}
