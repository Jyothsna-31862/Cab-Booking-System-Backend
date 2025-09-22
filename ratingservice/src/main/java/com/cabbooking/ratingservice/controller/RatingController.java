package com.cabbooking.ratingservice.controller;

import com.cabbooking.ratingservice.dto.RatingDTO;
import com.cabbooking.ratingservice.service.RatingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/ratings")
@Slf4j
public class RatingController {

    private final RatingService ratingService;

    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @PostMapping("/create")
    public ResponseEntity<RatingDTO> createRating(@RequestBody RatingDTO ratingDTO) {
        RatingDTO createdRating = ratingService.createRating(ratingDTO);
        return ResponseEntity.ok(createdRating);
    }

    @GetMapping("/driver/{driverId}/average")
    public ResponseEntity<Double> getAverageRatingForDriver(@PathVariable Integer driverId) {
        Double averageRating = ratingService.getAverageRatingForDriver(driverId);
        return ResponseEntity.ok(averageRating);
    }

    @GetMapping("/ride/{rideId}")
    public ResponseEntity<RatingDTO> getRatingByRideId(@PathVariable Integer rideId) {
        Optional<RatingDTO> rating = ratingService.getRatingById(rideId);
        return rating.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
    }
}
