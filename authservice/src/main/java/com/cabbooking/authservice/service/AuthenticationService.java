package com.cabbooking.authservice.service;

import com.cabbooking.authservice.dto.ForgotPassword;
import com.cabbooking.authservice.dto.LoginDto;
import com.cabbooking.authservice.dto.UserDto;

public interface AuthenticationService {
    String login(LoginDto loginDto);

    String register(UserDto userDto);
    String resetPassword(ForgotPassword forgotPassword);
}
