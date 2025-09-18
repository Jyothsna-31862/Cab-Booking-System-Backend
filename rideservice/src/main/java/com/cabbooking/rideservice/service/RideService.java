package com.cabbooking.rideservice.service;

import com.cabbooking.rideservice.dto.CancelDto;
import com.cabbooking.rideservice.dto.RideDto;
import com.cabbooking.rideservice.dto.SuccessResponseDto;
import com.cabbooking.rideservice.entity.Ride;
import org.hibernate.dialect.lock.OptimisticEntityLockException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public interface RideService {
      RideDto bookARide(RideDto rideDto);
      RideDto getRideById(String rideId);
      ArrayList<RideDto> getDriverRidesByStatus(String driverId,String status);
      ArrayList<RideDto> getUserRidesByStatus(String userId, String status);
      ArrayList<RideDto> getAllUserRides(String userId);
      ArrayList<RideDto> getAllDriverRides(String driverId);
      SuccessResponseDto updateRideStatus(String rideId, String status);
      RideDto getNewestImmediateRideForDriver(String driverId);
      SuccessResponseDto cancelRideStatus(String rideId, CancelDto cancelDto);
}
