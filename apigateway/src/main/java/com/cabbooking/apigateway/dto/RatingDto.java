package com.cabbooking.apigateway.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RatingDto {
    private String id;
    private String rideId;
    private String userId;
    private String driverId;
    private double rating;
    private String comment;
}
