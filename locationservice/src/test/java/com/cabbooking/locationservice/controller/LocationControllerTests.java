package com.cabbooking.locationservice.controller;

import com.cabbooking.locationservice.dto.LocationDto;
import com.cabbooking.locationservice.service.LocationService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.DisplayName.class)
public class LocationControllerTests {

    @Mock
    private LocationService locationService;

    @InjectMocks
    private LocationController locationController;

    @BeforeEach
    void logStart(TestInfo testInfo) {
        log.info("Starting test: {}", testInfo.getDisplayName());
    }

    @AfterEach
    void logEnd(TestInfo testInfo) {
        log.info("Finished test: {}", testInfo.getDisplayName());
    }

    @Test
    @DisplayName("1️. getLocations() should return OK and list of LocationDto")
    void getLocations_ReturnsOkAndListOfLocations() {
        List<LocationDto> locations = List.of(
                new LocationDto(1, "Koyambedu", "West Chennai", new BigDecimal("13.0694"), new BigDecimal("80.1948")),
                new LocationDto(2, "Guindy", "South and East Chennai", new BigDecimal("13.0067"), new BigDecimal("80.2206"))
        );

        when(locationService.getLocations()).thenReturn(locations);

        ResponseEntity<List<LocationDto>> response = locationController.getLocations();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(locations, response.getBody());
    }

    @Test
    @DisplayName("2️. getLocationByArea() should return OK and LocationDto")
    void getLocationByArea_ReturnsOkAndLocationDto() {
        String area = "koyambedu";
        LocationDto location = new LocationDto(1, area, "West Chennai", new BigDecimal("13.0694"), new BigDecimal("80.1948"));

        when(locationService.getLocationByArea(area)).thenReturn(location);

        ResponseEntity<LocationDto> response = locationController.getLocationByArea(area);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(location, response.getBody());
    }
}
