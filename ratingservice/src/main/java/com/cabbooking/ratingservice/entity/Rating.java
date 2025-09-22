package com.cabbooking.ratingservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Rating")
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer ratingId;

    private Integer rideId;

    private Integer userId;

    private Integer driverId;

    private Integer score;
    private String feedback;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
