package com.cabbooking.controller;
import com.cabbooking.dto.AvailabilityRequest;
import com.cabbooking.dto.DriverDto;
import com.cabbooking.dto.DriverRequest;
import com.cabbooking.dto.DriverServiceResponse;
import com.cabbooking.dto.PasswordResetRequest;
import com.cabbooking.dto.StatusUpdateRequest;
import com.cabbooking.exception.DriverNotFoundException;
import com.cabbooking.service.DriverService;
import lombok.AllArgsConstructor;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/drivers")
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:4200/")
public class DriverController {

    private final DriverService driverService;


    @GetMapping("/{id}")
    public ResponseEntity<DriverDto> getUserById(@PathVariable("id") String id) throws DriverNotFoundException {

        DriverDto DriverById = driverService.getDriverById(id);
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
    

    
    @GetMapping("/available")
    public ResponseEntity<DriverDto> getAvailableDrivers(@RequestBody AvailabilityRequest availabilityRequest) {
        DriverDto availableDrivers = driverService.getAvailableDrivers(availabilityRequest.getCarSeater());
        return new ResponseEntity<>(availableDrivers, HttpStatus.OK);
    }

    @PatchMapping("status/{id}")
    public ResponseEntity<DriverDto> updateAvailability(
            @PathVariable("id") String id) throws DriverNotFoundException {
      
        DriverDto updatedDriver = driverService.updateDriverStatus(id);
        return new ResponseEntity<>(updatedDriver, HttpStatus.OK);
}
}

