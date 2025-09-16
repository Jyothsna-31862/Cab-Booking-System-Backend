package com.cabbooking.authservice.serviceimpl;

import com.cabbooking.authservice.client.DriverClient;
import com.cabbooking.authservice.client.UserClient;
import com.cabbooking.authservice.dto.*;
import com.cabbooking.authservice.entity.User;
import com.cabbooking.authservice.exception.AuthenticationAPIException;
import com.cabbooking.authservice.repository.UserRepository;
import com.cabbooking.authservice.security.JwtTokenProvider;
import com.cabbooking.authservice.service.AuthenticationService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@AllArgsConstructor
@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserClient userClient;
    private final DriverClient driverClient;


    @Override
    public JwtResponse login(LoginDto loginDto) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginDto.getEmail(), loginDto.getPassword()
            ));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            JwtResponse jwtResponse = new JwtResponse();
            User user = userRepository.findByEmail(loginDto.getEmail()).orElseThrow(() -> new AuthenticationAPIException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));
            log.info("User found: {}", user);
            if (user.getRole().equalsIgnoreCase("driver")) {
                ResponseEntity<DriverDto> driverDtoResponse = driverClient.getDriverByEmail(user.getEmail());
                DriverDto driverDto = driverDtoResponse.getBody();
                jwtResponse.setId(driverDto.getDriverId());

            } else {
                ResponseEntity<UserDto> userDtoResponse = userClient.getUserByEmail(user.getEmail());
                UserDto userDto = userDtoResponse.getBody();
                jwtResponse.setId(userDto.getUserId());
            }
            jwtResponse.setAccessToken(jwtTokenProvider.generateToken(authentication));
            jwtResponse.setRole(user.getRole());
            jwtResponse.setMessage(user.getRole().equalsIgnoreCase("user") ? "User logged in successfully" : "Driver logged in successfully");
            return jwtResponse;

        } catch (Exception e) {
            throw new AuthenticationAPIException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }
    }

    @Override
    public ResponseEntity<UserServiceResponse> registerUser(UserRequest userRequest) {

        String hashedPassword = passwordEncoder.encode(userRequest.getPassword());
        userRequest.setPassword(hashedPassword);

        ResponseEntity<UserServiceResponse> userServiceResponse = userClient.registerUser(userRequest);

        if (userServiceResponse.getStatusCode() == HttpStatus.CREATED && userServiceResponse.getBody() != null) {
            UserDto userDto = userServiceResponse.getBody().getBody();

            User user = new User();
            user.setEmail(userDto.getEmail());
            user.setRole(userRequest.getRole());
            user.setPassword(hashedPassword);
            userRepository.save(user);
            return userServiceResponse;
        }

        return userServiceResponse;
    }

    @Override
    public ResponseEntity<DriverServiceResponse> registerDriver(DriverRequest driverRequest) {

        String hashedPassword = passwordEncoder.encode(driverRequest.getPassword());
        driverRequest.setPassword(hashedPassword);

        ResponseEntity<DriverServiceResponse> driverResponse = driverClient.registerDriver(driverRequest);

        if (driverResponse.getStatusCode() == HttpStatus.CREATED && driverResponse.getBody() != null) {
            DriverDto driverDto = driverResponse.getBody().getBody();

            User user = new User();
            user.setEmail(driverDto.getEmail());
            user.setRole(driverRequest.getRole());
            user.setPassword(hashedPassword);
            userRepository.save(user);
            return driverResponse;
        }

        return driverResponse;
    }

    @Override
    public String resetPassword(ForgotPassword forgotPassword) {
        User user = userRepository.findByEmail(forgotPassword.getEmail()).orElseThrow(() -> new AuthenticationAPIException(HttpStatus.BAD_REQUEST, "User with that email does not exist."));


        user.setPassword(passwordEncoder.encode(forgotPassword.getNewPassword()));

        userRepository.save(user);

        return "Password has been reset successfully.";
    }

    @Override
    @Transactional
    public ResponseEntity<String> deleteUserByEmail(String email) {

        ResponseEntity<String> response = userClient.deleteUser(email);

        if (response.getStatusCode() == HttpStatus.OK) {
            userRepository.deleteByEmail(email);
        }
        return response;

    }
}

