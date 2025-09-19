package com.cabbooking.authservice.client;

import com.cabbooking.authservice.config.FeignClientConfig;
import com.cabbooking.authservice.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(
        name = "user-service",
        url = "http://localhost:8085/api/users"
)
public interface UserClient {

    @PostMapping("/register")
    public ResponseEntity<UserServiceResponse> registerUser(@RequestBody UserRequest userRequest);

    @GetMapping("/email/{email}")
    public ResponseEntity<UserDto> getUserByEmail(@PathVariable("email") String email);

    @DeleteMapping("/delete/{email}")
    public ResponseEntity<String> deleteUser(@PathVariable("email") String email);

    @PutMapping("/forgot-password")
    public ResponseEntity<SuccessResponse> forgotPassword(@RequestBody ForgotPassword forgotPassword);


}
