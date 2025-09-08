package com.cabbooking.locationservice.service;

import com.cabbooking.locationservice.model.Location;
import com.cabbooking.locationservice.repository.LocationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocationServiceImp implements LocationService{

    private final LocationRepository locationRepository;

    public LocationServiceImp(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    @Override
    public List<Location> getLocations() {
        return locationRepository.findAll();
    }
}
