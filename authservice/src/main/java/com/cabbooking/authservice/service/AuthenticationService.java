package com.cabbooking.authservice.service;

import com.cabbooking.authservice.dto.*;
import org.springframework.http.ResponseEntity;

public interface AuthenticationService {
    JwtResponse login(LoginDto loginDto);

    ResponseEntity<UserServiceResponse> registerUser(UserRequest userRequest);
    ResponseEntity<DriverServiceResponse> registerDriver(DriverRequest driverResponse);
    String resetPassword(ForgotPassword forgotPassword);
    ResponseEntity<String> deleteUserByEmail(String email);
}
