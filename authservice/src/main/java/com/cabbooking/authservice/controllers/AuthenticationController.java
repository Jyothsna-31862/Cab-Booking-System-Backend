package com.cabbooking.authservice.controllers;

import com.cabbooking.authservice.dto.*;
import com.cabbooking.authservice.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200/")
@RequestMapping("api/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody LoginDto loginDto){
        JwtResponse jwtResponse = authenticationService.login(loginDto);
        return ResponseEntity.ok(jwtResponse);
    }

    @PostMapping("/register-user")
    public ResponseEntity<UserServiceResponse> register(@RequestBody UserRequest userRequest){

        return authenticationService.register(userRequest);
    }

//    @PostMapping("/register-driver")
//    public ResponseEntity<UserServiceResponse> registerDriver(@RequestBody UserRequest userRequest){
//        userRequest.setRole("DRIVER");
//        ResponseEntity<UserServiceResponse> userServiceResponse = authenticationService.register(userRequest);
//        return userServiceResponse;
//    }

    @PutMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPassword forgotPassword){
        String s = authenticationService.resetPassword(forgotPassword);
        return ResponseEntity.ok(s);
    }
}
