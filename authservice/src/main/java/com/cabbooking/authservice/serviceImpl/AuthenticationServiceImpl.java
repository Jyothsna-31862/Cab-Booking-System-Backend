package com.cabbooking.authservice.serviceImpl;

import com.cabbooking.authservice.dto.ForgotPassword;
import com.cabbooking.authservice.dto.LoginDto;
import com.cabbooking.authservice.dto.UserDto;
import com.cabbooking.authservice.entity.User;
import com.cabbooking.authservice.exception.AuthenticationAPIException;
import com.cabbooking.authservice.repository.UserRepository;
import com.cabbooking.authservice.security.JwtTokenProvider;
import com.cabbooking.authservice.service.AuthenticationService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private AuthenticationManager authenticationManager;
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private JwtTokenProvider jwtTokenProvider;


    @Override
    public String login(LoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginDto.getEmail(),loginDto.getPassword()
        ));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtTokenProvider.generateToken(authentication);
        return token;
    }

    @Override
    public String register(UserDto userDto) {

        if(userRepository.existsByEmail(userDto.getEmail())){
            throw new AuthenticationAPIException(HttpStatus.BAD_REQUEST,"User Already exists");
        }
        User user =new User();
        user.setEmail(userDto.getEmail());
        user.setRole(userDto.getRole());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        userRepository.save(user);
        return " user registered successfully";
    }

    @Override
    public String resetPassword(ForgotPassword forgotPassword) {
        User user = userRepository.findByEmail(forgotPassword.getEmail()).orElseThrow(() -> new AuthenticationAPIException(HttpStatus.BAD_REQUEST, "User with that email does not exist."));;


        user.setPassword(passwordEncoder.encode(forgotPassword.getNewPassword()));

        userRepository.save(user);

        return "Password has been reset successfully.";
    }
}
