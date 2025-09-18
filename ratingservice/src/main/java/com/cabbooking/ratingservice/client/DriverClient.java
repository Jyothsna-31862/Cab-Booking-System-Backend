package com.cabbooking.ratingservice.client;

import com.cabbooking.authservice.dto.DriverDto;
import com.cabbooking.authservice.dto.DriverRequest;
import com.cabbooking.authservice.dto.DriverServiceResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(
        name = "driver-service",
        url = "http://localhost:8081/api/drivers"
)
public interface DriverClient {

    @PostMapping("/register")
    public ResponseEntity<DriverServiceResponse> registerDriver(@RequestBody DriverRequest driver);

    @GetMapping("/email/{email}")
    public ResponseEntity<DriverDto> getDriverByEmail(@PathVariable("email") String email);
}
