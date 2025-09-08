package com.cabbooking.controller;

import com.cabbooking.entity.Driver;
import com.cabbooking.service.DriverService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/drivers")
@AllArgsConstructor
public class DriverController {

    private final DriverService driverService;

    @PostMapping("/add")
    public ResponseEntity<Driver> registerDriver(@RequestBody Driver driver) {
        System.out.println("Received driver object: " + driver);
        Driver newDriver = driverService.registerDriver(driver);
        return new ResponseEntity<>(newDriver, HttpStatus.CREATED);
    }
}

