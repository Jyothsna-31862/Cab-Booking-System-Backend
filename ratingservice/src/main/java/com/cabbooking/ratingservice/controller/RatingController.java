package com.cabbooking.ratingservice.controller;


import com.cabbooking.ratingservice.dto.ApiResponse;
import com.cabbooking.ratingservice.dto.RatingDTO;
import com.cabbooking.ratingservice.service.RatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ratings")
@RequiredArgsConstructor
public class RatingController {
    private final RatingService ratingService;

    @PostMapping
    public ResponseEntity<ApiResponse> createRating(@RequestBody RatingDTO ratingDTO) {
        RatingDTO newRating = ratingService.createRating(ratingDTO);
        ApiResponse apiResponse = new ApiResponse(true, "Rating created successfully", java.time.LocalDateTime.now());
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }
}