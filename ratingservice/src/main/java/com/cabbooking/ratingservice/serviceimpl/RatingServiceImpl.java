package com.cabbooking.ratingservice.serviceimpl;

import com.cabbooking.ratingservice.dto.RatingDTO;
import com.cabbooking.ratingservice.entity.Rating;
import com.cabbooking.ratingservice.repository.RatingRepository;
import com.cabbooking.ratingservice.service.RatingService;
import com.cabbooking.ratingservice.exception.InvalidRatingException;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class RatingServiceImpl implements RatingService {
    private final RatingRepository ratingRepository;
    private final ModelMapper modelMapper;

    public RatingServiceImpl(RatingRepository ratingRepository, ModelMapper modelMapper) {
        this.ratingRepository = ratingRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public RatingDTO createRating(RatingDTO ratingDTO) {
        log.info("Attempting to create a new rating for rideId: {}", ratingDTO.getRideId());

        if (ratingDTO.getScore() == null || ratingDTO.getScore() < 1 || ratingDTO.getScore() > 5) {
            log.error("Invalid score provided. Score must be between 1 and 5.");
            throw new InvalidRatingException("Score must be between 1 and 5.");
        }

        Rating rating = modelMapper.map(ratingDTO, Rating.class);
        log.debug("Mapped DTO to entity: {}", rating);

        Rating savedRating;
        try {
            savedRating = ratingRepository.save(rating);
            log.info("Rating successfully saved with ID: {}", savedRating.getRatingId());
        } catch (Exception e) {
            log.error("Failed to save rating to the database.", e);
            throw new RuntimeException("An error occurred while saving the rating.");
        }


        return modelMapper.map(savedRating, RatingDTO.class);
    }
}