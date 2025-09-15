package com.cabbooking.authservice.service;

import com.cabbooking.authservice.dto.*;
import org.springframework.http.ResponseEntity;

public interface AuthenticationService {
    JwtResponse login(LoginDto loginDto);

    ResponseEntity<UserServiceResponse> register(UserRequest userRequest);
    String resetPassword(ForgotPassword forgotPassword);
}
