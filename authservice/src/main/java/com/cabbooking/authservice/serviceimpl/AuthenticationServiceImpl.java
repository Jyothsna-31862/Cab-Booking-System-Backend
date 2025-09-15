package com.cabbooking.authservice.serviceimpl;

import com.cabbooking.authservice.client.UserClient;
import com.cabbooking.authservice.dto.*;
import com.cabbooking.authservice.entity.User;
import com.cabbooking.authservice.exception.AuthenticationAPIException;
import com.cabbooking.authservice.repository.UserRepository;
import com.cabbooking.authservice.security.JwtTokenProvider;
import com.cabbooking.authservice.service.AuthenticationService;
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


    @Override
    public JwtResponse login(LoginDto loginDto) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginDto.getEmail(),loginDto.getPassword()
            ));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            JwtResponse jwtResponse = new JwtResponse();
            User user = userRepository.findByEmail(loginDto.getEmail()).orElseThrow(() -> new AuthenticationAPIException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));
            jwtResponse.setAccessToken(jwtTokenProvider.generateToken(authentication));
            jwtResponse.setRole(user.getRole());
            jwtResponse.setMessage(user.getRole().equalsIgnoreCase("user") ? "User logged in successfully" : "Driver logged in successfully");
            return jwtResponse;

        }catch (Exception e){
            throw new AuthenticationAPIException(HttpStatus.UNAUTHORIZED,"Invalid email or password");
        }
    }

    @Override
    public ResponseEntity<UserServiceResponse> register(UserRequest userRequest) {

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
    public String resetPassword(ForgotPassword forgotPassword) {
        User user = userRepository.findByEmail(forgotPassword.getEmail()).orElseThrow(() -> new AuthenticationAPIException(HttpStatus.BAD_REQUEST, "User with that email does not exist."));;


        user.setPassword(passwordEncoder.encode(forgotPassword.getNewPassword()));

        userRepository.save(user);

        return "Password has been reset successfully.";
    }
}
