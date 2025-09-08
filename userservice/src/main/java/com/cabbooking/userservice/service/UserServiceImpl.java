package com.cabbooking.userservice.service;

import com.cabbooking.userservice.config.MapperConfig;
import com.cabbooking.userservice.dto.UserDto;
import com.cabbooking.userservice.dto.UserRequest;
import com.cabbooking.userservice.dto.UserResponse;
import com.cabbooking.userservice.exception.EmailAlreadyExistsException;
import com.cabbooking.userservice.exception.PhoneAlreadyExistsException;
import com.cabbooking.userservice.model.User;
import com.cabbooking.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final MapperConfig mapperConfig;


    @Override
    public UserResponse registerUser(UserRequest userRequest) {


        log.info("UserService class invoked");

        if (userRepository.existsByEmail(userRequest.getEmail())) {
            throw new EmailAlreadyExistsException("Given Email Already Registered: " + userRequest.getEmail());
        }

        if (userRepository.existsByPhone(userRequest.getPhone())) {
            throw new PhoneAlreadyExistsException("Given Phone number already registered: " + userRequest.getPhone());
        }

        User user;
        user = modelMapper.map(userRequest, User.class);


        user.setCode(mapperConfig.generateCode());
        User savedUser = userRepository.save(user);



        log.info("User Saved Successfully");

        UserDto userDto=modelMapper.map(savedUser, UserDto.class);

        UserResponse userResponse=new UserResponse();

        userResponse.setBody(userDto);
        userResponse.setStatus("success");
        userResponse.setMessage("User Registered Successfully");

        return userResponse;

    }
}
