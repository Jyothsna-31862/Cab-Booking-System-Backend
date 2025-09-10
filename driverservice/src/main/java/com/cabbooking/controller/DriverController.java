package com.cabbooking.controller;
import com.cabbooking.dto.DriverRequest;
import com.cabbooking.dto.DriverServiceResponse;
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

    @PostMapping("/register")
    public ResponseEntity<DriverServiceResponse> registerDriver(@RequestBody DriverRequest driver) {
        System.out.println("Received driver object: " + driver);
        DriverServiceResponse newDriver = driverService.registerDriver(driver);
        return new ResponseEntity<>(newDriver, HttpStatus.CREATED);
    }
}

