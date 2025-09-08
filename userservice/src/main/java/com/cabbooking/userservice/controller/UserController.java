package com.cabbooking.userservice.controller;

import com.cabbooking.userservice.dto.UserRequest;
import com.cabbooking.userservice.dto.UserResponse;
import com.cabbooking.userservice.model.User;
import com.cabbooking.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {


    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(@RequestBody UserRequest userRequest) {

            log.info("User request {}",userRequest);
            UserResponse savedUser = userService.registerUser(userRequest);
            return new ResponseEntity<>(savedUser, HttpStatus.CREATED);

    }
}
