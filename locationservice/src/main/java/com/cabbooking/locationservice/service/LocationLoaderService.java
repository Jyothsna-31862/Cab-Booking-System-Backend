package com.cabbooking.locationservice.service;

import com.cabbooking.locationservice.model.Location;
import com.cabbooking.locationservice.repository.LocationRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocationLoaderService {

    private final LocationRepository locationRepository;
    private final ObjectMapper objectMapper;


    @PostConstruct
    public void loadLocationsFromJson() {
        log.info("Checking if location data needs to be loaded...");
        if (locationRepository.count() > 0) {
            log.info("Locations already exist. Skipping load.");
            return;
        }

        try {
            InputStream inputStream = getClass().getResourceAsStream("/locations.json");
            if (inputStream == null) {
                log.error("locations.json not found in classpath.");
                return;
            }

            List<Location> locations = Arrays.asList(objectMapper.readValue(inputStream, Location[].class));
            locationRepository.saveAll(locations);
            log.info("Locations inserted successfully.");
        } catch (IOException e) {
            log.error("Failed to load locations", e);
        }
    }

}