package com.cabbooking.ratingservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RatingDTO {
    private Integer ratingId;
    private Integer rideId;
    private Integer userId;
    private Integer driverId;
    private Byte score;
    private String comments;
}
