package com.cabbooking.ratingservice.service;

import com.cabbooking.ratingservice.dto.RatingDTO;
import org.springframework.stereotype.Service;


public interface RatingService {
     RatingDTO createRating(RatingDTO ratingDTO);
}
