package com.cabbooking.rideservice.repository;

import com.cabbooking.rideservice.entity.Ride;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RideRepository extends JpaRepository<Ride,String> {
    List<Ride> findAllByUserIdOrderByBookingDateDescBookingTimeDesc(String id);
    List<Ride> findAllByDriverIdOrderByBookingDateDescBookingTimeDesc(String id);
    Optional<Ride> findFirstByDriverIdAndStatusOrderByRequestedAtDesc(String driverId, String status);
    List<Ride> findAllByDriverIdAndStatus(String driverId, String status);
    List<Ride> findAllByUserIdAndStatus(String userId, String status);
}
