package com.cabbooking.rideservice.repository;

import com.cabbooking.rideservice.dto.RideDto;
import com.cabbooking.rideservice.entity.Ride;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;
import java.util.Optional;

public interface RideRepository extends JpaRepository<Ride,String> {
    ArrayList<Ride> findAllByUserId(String id);
    ArrayList<Ride> findAllByDriverId(String id);
    Optional<Ride> findFirstByDriverIdAndStatusOrderByRequestedAtDesc(String driverId, String status);
    ArrayList<Ride> findAllByDriverIdAndStatus(String driverId, String status);
    ArrayList<Ride> findAllByUserIdAndStatus(String userId, String status);
}
