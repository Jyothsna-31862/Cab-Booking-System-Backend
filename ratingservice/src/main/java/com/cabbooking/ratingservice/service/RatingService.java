package com.cabbooking.ratingservice.service;

import com.cabbooking.ratingservice.dto.RatingDTO;
import org.springframework.stereotype.Service;

import java.util.Optional;


public interface RatingService {
     RatingDTO createRating(RatingDTO ratingDTO);
     Double getAverageRatingForDriver(Integer driverId);
     Optional<RatingDTO> getRatingById(Integer rideId);
}
