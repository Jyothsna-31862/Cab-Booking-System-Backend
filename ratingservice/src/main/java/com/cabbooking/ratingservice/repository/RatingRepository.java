package com.cabbooking.ratingservice.repository;

import com.cabbooking.ratingservice.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RatingRepository extends JpaRepository<Rating, Integer> {
}
