package com.cabbooking.authservice.client;

import com.cabbooking.authservice.config.FeignClientConfig;
import com.cabbooking.authservice.dto.UserRequest;
import com.cabbooking.authservice.dto.UserServiceResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "user-service",
        url = "http://localhost:8085/users"
)
public interface UserClient {

    @PostMapping("/register")
    public ResponseEntity<UserServiceResponse> registerUser(@RequestBody UserRequest userRequest);

}
