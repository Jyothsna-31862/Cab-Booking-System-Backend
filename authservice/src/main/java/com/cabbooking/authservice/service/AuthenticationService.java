package com.cabbooking.authservice.service;

import com.cabbooking.authservice.dto.*;
import org.springframework.http.ResponseEntity;

public interface AuthenticationService {
    JwtResponse login(LoginDto loginDto);

    UserServiceResponse registerUser(UserRequest userRequest);
    DriverServiceResponse registerDriver(DriverRequest driverResponse);
    SuccessResponse resetPassword(ForgotPassword forgotPassword);
    String deleteUserByEmail(String email);
    AuthResponse validateToken(ValidateTokenRequest tokenRequest);
}
