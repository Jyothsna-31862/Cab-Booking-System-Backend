package com.cabbooking.apigateway.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationDto {
    private String id;
    private String driverId;
    private double latitude;
    private double longitude;
    private String timestamp;
    private boolean available;
}
