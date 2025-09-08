package com.cabbooking.userservice.service;

import com.cabbooking.userservice.dto.UserRequest;
import com.cabbooking.userservice.dto.UserResponse;
import org.springframework.stereotype.Service;

@Service
public interface UserService
{
    public UserResponse registerUser(UserRequest userRequest);
}
