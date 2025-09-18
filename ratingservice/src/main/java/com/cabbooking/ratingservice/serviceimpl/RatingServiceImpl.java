package com.cabbooking.ratingservice.serviceimpl;

import com.cabbooking.ratingservice.dto.RatingDTO;
import com.cabbooking.ratingservice.entity.Rating;
import com.cabbooking.ratingservice.repository.RatingRepository;
import com.cabbooking.ratingservice.service.RatingService;
import com.cabbooking.ratingservice.exception.InvalidRatingException;
import com.cabbooking.ratingservice.client.DriverServiceClient;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class RatingServiceImpl implements RatingService {
    private final RatingRepository ratingRepository;
    private final ModelMapper modelMapper;
    private final DriverServiceClient driverServiceClient;

    public RatingServiceImpl(RatingRepository ratingRepository, ModelMapper modelMapper, DriverServiceClient driverServiceClient) {
        this.ratingRepository = ratingRepository;
        this.modelMapper = modelMapper;
        this.driverServiceClient = driverServiceClient;
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

        Rating savedRating = null;
        try {
            savedRating = ratingRepository.save(rating);
            log.info("Rating successfully saved with ID: {}", savedRating.getRatingId());

            // After saving rating, update driver's average rating
            updateDriverAverageRating(savedRating.getDriverId());

        } catch (Exception e) {
            log.error("Failed to save rating to the database.", e);
            throw new RuntimeException("An error occurred while saving the rating.");
        }

        return modelMapper.map(savedRating, RatingDTO.class);
    }

    private void updateDriverAverageRating(Integer driverId) {
        try {
            log.info("Updating average rating for driver: {}", driverId);

            // Calculate new average rating for the driver
            Double newAverageRating = getAverageRatingForDriver(driverId);

            // Call driver service to update the rating directly
            driverServiceClient.updateDriverRating(driverId.toString(), newAverageRating);

            log.info("Successfully updated driver {} with new average rating: {}", driverId, newAverageRating);

        } catch (Exception e) {
            log.error("Failed to update driver average rating for driver {}: {}", driverId, e.getMessage());
            // Don't fail the rating creation if driver update fails
        }
    }

    @Override
    public Double getAverageRatingForDriver(Integer driverId) {
        Double averageRating = ratingRepository.findAverageRatingByDriverId(driverId);
        log.info("Calculated average rating for driverId {}: {}", driverId, averageRating);
        return averageRating != null ? averageRating : 0.0;
    }

    @Override
    public Optional<RatingDTO> getRatingById(Integer rideId) {
        log.info("Attempting to retrieve rating for rideId: {}", rideId);
        Optional<Rating> ratingOptional = ratingRepository.findByRideId(rideId);

        if (ratingOptional.isPresent()) {
            Rating rating = ratingOptional.get();
            log.info("Retrieved rating for rideId {}: {}", rideId, rating);
            return Optional.of(modelMapper.map(rating, RatingDTO.class));
        } else {
            log.warn("No rating found for rideId: {}", rideId);
            return Optional.empty();
        }
    }

}