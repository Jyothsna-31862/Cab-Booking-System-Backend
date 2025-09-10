package com.cabbooking.authservice.controllers;

import com.cabbooking.authservice.dto.ForgotPassword;
import com.cabbooking.authservice.dto.JwtResponse;
import com.cabbooking.authservice.dto.LoginDto;
import com.cabbooking.authservice.dto.UserDto;
import com.cabbooking.authservice.service.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
  public ResponseEntity<JwtResponse> login(@RequestBody LoginDto loginDto){
    String token = authenticationService.login(loginDto);
    JwtResponse jwtResponse = new JwtResponse();
    jwtResponse.setAccessToken(token);
    return ResponseEntity.ok(jwtResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserDto userDto){
        String s = authenticationService.register(userDto);
        return ResponseEntity.ok(s);
    }

    @PutMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPassword forgotPassword){
        String s = authenticationService.resetPassword(forgotPassword);
        return ResponseEntity.ok(s);
    }
}
