package com.cabbooking.ratingservice.controller;

import com.cabbooking.ratingservice.dto.RatingDTO;
import com.cabbooking.ratingservice.service.RatingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/ratings")
@Slf4j
@RequiredArgsConstructor
public class RatingController {

    private final RatingService ratingService;

    @PostMapping
    public ResponseEntity<RatingDTO> createRating(@RequestBody RatingDTO ratingDTO) {
        log.info("Received rating: {}", ratingDTO);
        RatingDTO createdRating = ratingService.createRating(ratingDTO);
        return ResponseEntity.ok(createdRating);
    }

    @GetMapping("/driver/{driverId}")
    public ResponseEntity<Double> getAverageRatingForDriver(@PathVariable String driverId) {
        Double averageRating = ratingService.getAverageRatingForDriver(driverId);
        return ResponseEntity.ok(averageRating);
    }

}
