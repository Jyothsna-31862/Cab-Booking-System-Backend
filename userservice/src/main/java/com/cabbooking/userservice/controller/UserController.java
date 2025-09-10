package com.cabbooking.userservice.controller;

import com.cabbooking.userservice.dto.UserRequest;
import com.cabbooking.userservice.dto.UserServiceResponse;
import com.cabbooking.userservice.exception.UserNotFoundException;
import com.cabbooking.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public ResponseEntity<UserServiceResponse> registerUser(@RequestBody UserRequest userRequest) {

            log.info("User request {}",userRequest);
            UserServiceResponse savedUser = userService.registerUser(userRequest);
            return new ResponseEntity<>(savedUser, HttpStatus.CREATED);

    }


    @GetMapping("/{id}")
    public ResponseEntity<UserServiceResponse> getUserById(@PathVariable("id") String id) throws UserNotFoundException {

        UserServiceResponse userById = userService.getUserById(id);
        return new ResponseEntity<>(userById, HttpStatus.OK);

    }
}
