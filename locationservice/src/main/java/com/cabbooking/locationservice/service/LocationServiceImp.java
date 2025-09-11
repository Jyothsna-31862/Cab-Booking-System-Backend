package com.cabbooking.locationservice.service;

import com.cabbooking.locationservice.dto.LocationDto;
import com.cabbooking.locationservice.repository.LocationRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LocationServiceImp implements LocationService{

    private final LocationRepository locationRepository;
    private final ModelMapper modelMapper;

    public LocationServiceImp(LocationRepository locationRepository, ModelMapper modelMapper) {
        this.locationRepository = locationRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<LocationDto> getLocations() {
        return locationRepository.findAll()
                .stream()

                .map(location -> modelMapper.map(location, LocationDto.class))
                .collect(Collectors.toList());
    }

}
