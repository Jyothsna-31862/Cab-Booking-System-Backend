package com.cabbooking.controller;
import com.cabbooking.dto.DriverDto;
import com.cabbooking.dto.DriverRequest;
import com.cabbooking.dto.DriverServiceResponse;
import com.cabbooking.dto.PasswordResetRequest;
import com.cabbooking.exception.DriverNotFoundException;
import com.cabbooking.service.DriverService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/drivers")
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:4200/")
@Slf4j
public class DriverController {

    private final DriverService driverService;


    @GetMapping("/{id}")
    public ResponseEntity<DriverDto> getUserById(@PathVariable("id") String id) throws DriverNotFoundException {

        DriverDto DriverById = driverService.getDriverById(id);
        log.info("Fetched driver with ID: {}", DriverById);
        return new ResponseEntity<>(DriverById, HttpStatus.OK);

    }


    @PostMapping("/register")
    public ResponseEntity<DriverServiceResponse> registerDriver(@RequestBody DriverRequest driver) {
        System.out.println("Received driver object: " + driver);
        DriverServiceResponse newDriver = driverService.registerDriver(driver);
        return new ResponseEntity<>(newDriver, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DriverDto> updateDriverProfile(
            @PathVariable("id") String id,
            @RequestBody DriverRequest driverRequest) throws DriverNotFoundException {
        DriverDto updatedDriver = driverService.updateDriverProfile(id, driverRequest);
        return new ResponseEntity<>(updatedDriver, HttpStatus.OK);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDriver(@PathVariable("id") String id) throws DriverNotFoundException {
        driverService.deleteDriver(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    
    @PostMapping("/forgot-password")
    public ResponseEntity<DriverServiceResponse> forgotPassword(@RequestBody PasswordResetRequest request) {
        DriverServiceResponse response = driverService.forgotPassword(request.getEmail(),request.getNewPassword());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    

    
    @GetMapping(value = "/available",params = "carSeater" )
    public ResponseEntity<DriverDto> getAvailableDrivers(@RequestParam("carSeater") String carSeater) {
        DriverDto availableDrivers = driverService.getAvailableDrivers(carSeater);
        return new ResponseEntity<>(availableDrivers, HttpStatus.OK);
    }

    @PatchMapping("status/{id}")
    public ResponseEntity<DriverDto> updateAvailability(
            @PathVariable("id") String id) throws DriverNotFoundException {
      
        DriverDto updatedDriver = driverService.updateDriverStatus(id);
        return new ResponseEntity<>(updatedDriver, HttpStatus.OK);
    }

    @Operation(summary = "Get driver by Email")
    @GetMapping("/email/{email}")
    public ResponseEntity<DriverDto> getDriverByEmail(@PathVariable("email") String email) throws DriverNotFoundException {
        DriverDto driverByEmail = driverService.getDriverByEmail(email);
        return new ResponseEntity<>(driverByEmail, HttpStatus.OK);
    }

    @PutMapping(value="/{id}/rating",params = "rating")
    public ResponseEntity<DriverDto> updateDriverRating(
            @PathVariable("id") String id,
            @RequestParam("rating") double rating) throws DriverNotFoundException {
        DriverDto updatedDriver = driverService.updateDriverRating(id, rating);
        return new ResponseEntity<>(updatedDriver, HttpStatus.OK);
    }
}
