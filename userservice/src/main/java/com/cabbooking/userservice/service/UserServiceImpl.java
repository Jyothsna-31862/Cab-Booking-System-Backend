package com.cabbooking.userservice.service;

import com.cabbooking.userservice.config.MapperConfig;
import com.cabbooking.userservice.dto.UserDto;
import com.cabbooking.userservice.dto.UserRequest;
import com.cabbooking.userservice.dto.UserServiceResponse;
import com.cabbooking.userservice.exception.EmailAlreadyExistsException;
import com.cabbooking.userservice.exception.PhoneAlreadyExistsException;
import com.cabbooking.userservice.exception.UserNotFoundException;
import com.cabbooking.userservice.model.User;
import com.cabbooking.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;


@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final MapperConfig mapperConfig;

    @Override
    public UserServiceResponse registerUser(UserRequest userRequest) {
        log.info("Registering user with email: {}", userRequest.getEmail());

        if (userRepository.existsByEmail(userRequest.getEmail())) {
            log.warn("Email already registered: {}", userRequest.getEmail());
            throw new EmailAlreadyExistsException("Given Email Already Registered: " + userRequest.getEmail());
        }

        if (userRepository.existsByPhone(userRequest.getPhone())) {
            log.warn("Phone number already registered: {}", userRequest.getPhone());
            throw new PhoneAlreadyExistsException("Given Phone number already registered: " + userRequest.getPhone());
        }

        User user = modelMapper.map(userRequest, User.class);
        user.setCode(mapperConfig.generateCode());
        User savedUser = userRepository.save(user);

        log.info("User registered successfully with ID: {}", savedUser.getUserId());

        UserDto userDto = modelMapper.map(savedUser, UserDto.class);
        UserServiceResponse userResponse = new UserServiceResponse();
        userResponse.setBody(userDto);
        userResponse.setStatus("success");
        userResponse.setMessage("User Registered Successfully");

        return userResponse;
    }

    @Override
    public UserDto getUserById(String id) throws UserNotFoundException {
        log.info("Fetching user with ID: {}", id);

        User user = userRepository.findByUserId(id)
                .orElseThrow(() -> {
                    log.error("User not found with ID: {}", id);
                    return new UserNotFoundException("User with ID " + id + " not found");
                });

        return modelMapper.map(user, UserDto.class);
    }

    @Override
    public UserDto getUserByEmail(String email) throws UserNotFoundException {
        log.info("Fetching user with email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("User not found with email: {}", email);
                    return new UserNotFoundException("User with email " + email + " not found");
                });

        return modelMapper.map(user, UserDto.class);
    }
}

