package com.cabbooking.locationservice.controller;

import com.cabbooking.locationservice.dto.LocationDto;
import com.cabbooking.locationservice.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/locations")
@CrossOrigin("http://localhost:4200/")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    @GetMapping
    public ResponseEntity<List<LocationDto>> getLocations(){
        List<LocationDto> locations = locationService.getLocations();
        return new ResponseEntity<>(locations, HttpStatus.OK);
    }
}
