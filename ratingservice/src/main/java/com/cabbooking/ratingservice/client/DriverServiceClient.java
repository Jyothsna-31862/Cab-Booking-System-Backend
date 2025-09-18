package com.cabbooking.ratingservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "DRIVER-SERVICE", url = "http://localhost:8082")
public interface DriverServiceClient {

    @PatchMapping("/api/drivers/rating/{driverId}")
    ResponseEntity<Void> updateDriverRating(
            @PathVariable("driverId") String driverId,
            @RequestParam("rating") Double rating
    );
}

