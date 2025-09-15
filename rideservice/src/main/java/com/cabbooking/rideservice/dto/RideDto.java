package com.cabbooking.rideservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RideDto {
    private Integer rideId;

    private Integer userId;
    private Integer driverId;

    private String pickupLocation;
    private String dropLocation;

    private String status;

    private Double distance;
    private Double fare;
}
