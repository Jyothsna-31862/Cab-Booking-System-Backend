package com.cabbooking.authservice.serviceimpl;

import com.cabbooking.authservice.client.DriverClient;
import com.cabbooking.authservice.client.UserClient;
import com.cabbooking.authservice.dto.*;
import com.cabbooking.authservice.entity.User;
import com.cabbooking.authservice.exception.AuthenticationAPIException;
import com.cabbooking.authservice.repository.UserRepository;
import com.cabbooking.authservice.security.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private UserClient userClient;
    @Mock
    private DriverClient driverClient;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    private UserDto createMockUserDto() {
        return new UserDto("user-1", "Test User", "user@email.com", "1234567890", "Male", 200);
    }

    private DriverDto createMockDriverDto(String driverId, String email) {
        DriverDto dto = new DriverDto();
        dto.setDriverId(driverId);
        dto.setEmail(email);
        dto.setFullName("Test Driver");
        dto.setPhone("0987654321");
        dto.setLicenceNumber("DL12345");
        dto.setGender("Female");
        dto.setVehicleNumber("MH12AB1234");
        dto.setVehicleName("Car");
        dto.setCarSeater("4");
        dto.setRating(4.5);
        dto.setAvailable(true);
        return dto;
    }


    @Test
    @DisplayName("login_user_success: should return user JWT response on valid user login")
    void login_user_success() {
        LoginDto loginDto = new LoginDto("user@email.com", "password");
        User user = new User(1, "user@email.com", "encodedPassword", "user");
        Authentication authentication = mock(Authentication.class);
        UserDto userDto = createMockUserDto();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(userRepository.findByEmail(loginDto.getEmail())).thenReturn(Optional.of(user));
        when(userClient.getUserByEmail(loginDto.getEmail())).thenReturn(ResponseEntity.ok(userDto));
        when(jwtTokenProvider.generateToken(authentication)).thenReturn("token");

        JwtResponse response = authenticationService.login(loginDto);

        assertNotNull(response);
        assertEquals("user", response.getRole());
        assertEquals("User logged in successfully", response.getMessage());
        assertEquals("token", response.getAccessToken());
        assertEquals("user-1", response.getId());

        verify(userClient, times(1)).getUserByEmail(loginDto.getEmail());
        verify(driverClient, never()).getDriverByEmail(anyString());
    }

    @Test
    @DisplayName("login_driver_success: should return driver JWT response on valid driver login")
    void login_driver_success() {
        LoginDto loginDto = new LoginDto("driver@email.com", "password");
        User user = new User(1, "driver@email.com", "encodedPassword", "driver");
        Authentication authentication = mock(Authentication.class);
        DriverDto driverDto = createMockDriverDto("driver-1", "driver@email.com");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(userRepository.findByEmail(loginDto.getEmail())).thenReturn(Optional.of(user));
        when(driverClient.getDriverByEmail(loginDto.getEmail())).thenReturn(ResponseEntity.ok(driverDto));
        when(jwtTokenProvider.generateToken(authentication)).thenReturn("token");

        JwtResponse response = authenticationService.login(loginDto);

        assertNotNull(response);
        assertEquals("driver", response.getRole());
        assertEquals("Driver logged in successfully", response.getMessage());
        assertEquals("token", response.getAccessToken());
        assertEquals("driver-1", response.getId());

        verify(driverClient, times(1)).getDriverByEmail(loginDto.getEmail());
        verify(userClient, never()).getUserByEmail(anyString());
    }

    @Test
    @DisplayName("login_user_client_exception_throws_api_exception: should throw AuthenticationAPIException on UserClient failure")
    void login_user_client_exception_throws_api_exception() {
        LoginDto loginDto = new LoginDto("user@email.com", "password");
        User user = new User(1, "user@email.com", "encodedPassword", "user");
        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(userRepository.findByEmail(loginDto.getEmail())).thenReturn(Optional.of(user));
        when(userClient.getUserByEmail(loginDto.getEmail())).thenThrow(new RuntimeException("Client Error"));

        AuthenticationAPIException exception = assertThrows(AuthenticationAPIException.class, () ->
                authenticationService.login(loginDto)
        );

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
        assertEquals("Invalid email or password", exception.getMessage());
    }

    @Test
    @DisplayName("login_invalid_credentials_throws_exception: should throw AuthenticationAPIException on invalid credentials")
    void login_invalid_credentials_throws_exception() {
        LoginDto loginDto = new LoginDto("invalid@email.com", "wrong");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        AuthenticationAPIException exception = assertThrows(AuthenticationAPIException.class, () ->
                authenticationService.login(loginDto)
        );

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
        assertEquals("Invalid email or password", exception.getMessage());

        verify(userRepository, never()).findByEmail(anyString());
    }

    @Test
    @DisplayName("login_user_not_found_throws_exception: should throw AuthenticationAPIException if user is not found in DB after successful authentication")
    void login_user_not_found_throws_exception() {
        LoginDto loginDto = new LoginDto("valid@email.com", "password");
        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(userRepository.findByEmail(loginDto.getEmail())).thenReturn(Optional.empty());

        AuthenticationAPIException exception = assertThrows(AuthenticationAPIException.class, () ->
                authenticationService.login(loginDto)
        );

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
        assertEquals("Invalid email or password", exception.getMessage());

        verify(userRepository, times(1)).findByEmail(loginDto.getEmail());
        verify(userClient, never()).getUserByEmail(anyString());
    }

    @Test
    @DisplayName("registerUser_success: should register a new user")
    void registerUser_success() {
        UserRequest userRequest = new UserRequest("NewUser", "newUser@email.com", "1234567890", "pass123", "Male", 200, "user");

        UserDto userDto = new UserDto("user-2", userRequest.getFullName(), userRequest.getEmail(), userRequest.getPhone(), userRequest.getGender(), userRequest.getCode());

        UserServiceResponse clientResponse = new UserServiceResponse(userDto, "success", "User registered successfully");

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userClient.registerUser(any(UserRequest.class))).thenReturn(new ResponseEntity<>(clientResponse, HttpStatus.CREATED));

        UserServiceResponse response = authenticationService.registerUser(userRequest);

        assertNotNull(response);
        assertEquals("User registered successfully", response.getMessage());
        assertEquals("success", response.getStatus());

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("registerUser_already_exists_throws_exception: should throw AuthenticationAPIException if email already exists")
    void registerUser_already_exists_throws_exception() {
        UserRequest userRequest = new UserRequest("NewUser", "exists@email.com", "1234567890", "pass123", "Male", 200, "user");

        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        AuthenticationAPIException exception = assertThrows(AuthenticationAPIException.class, () ->
                authenticationService.registerUser(userRequest)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertTrue(exception.getMessage().contains("Given Email Already Registered"));

        verify(passwordEncoder, never()).encode(anyString());
        verify(userClient, never()).registerUser(any(UserRequest.class));
    }

    @Test
    @DisplayName("registerUser_client_response_null_body_throws_exception: should throw APIException if client returns CREATED but body/UserDto is null")
    void registerUser_client_response_null_body_throws_exception() {
        UserRequest userRequest = new UserRequest("NewUser", "newUser@email.com", "1234567890", "pass123", "Male", 200, "user");

        UserServiceResponse clientResponseWithNullBody = new UserServiceResponse(null, "success", "Registered but no details");

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userClient.registerUser(any(UserRequest.class))).thenReturn(new ResponseEntity<>(clientResponseWithNullBody, HttpStatus.CREATED));

        AuthenticationAPIException exception = assertThrows(AuthenticationAPIException.class, () ->
                authenticationService.registerUser(userRequest)
        );

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatus());
        assertTrue(exception.getMessage().contains("User details not found in response"));
    }

    @Test
    @DisplayName("registerUser_client_not_created_status: should return response body when client status is not CREATED")
    void registerUser_client_not_created_status() {
        UserRequest userRequest = new UserRequest("NewUser", "newUser@email.com", "1234567890", "pass123", "Male", 200, "user");

        UserDto userDto = new UserDto("user-2", userRequest.getFullName(), userRequest.getEmail(), userRequest.getPhone(), userRequest.getGender(), userRequest.getCode());

        UserServiceResponse clientResponse = new UserServiceResponse(userDto, "error", "Bad Request from Client");

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userClient.registerUser(any(UserRequest.class))).thenReturn(new ResponseEntity<>(clientResponse, HttpStatus.BAD_REQUEST));

        UserServiceResponse response = authenticationService.registerUser(userRequest);

        assertNotNull(response);
        assertEquals("Bad Request from Client", response.getMessage());

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("registerDriver_success: should register a new driver")
    void registerDriver_success() {
        DriverRequest driverRequest = new DriverRequest();
        driverRequest.setFullName("NewDriver");
        driverRequest.setEmail("newDriver@email.com");
        driverRequest.setPhone("1234567890");
        driverRequest.setPassword("pass123");
        driverRequest.setLicenceNumber("DL12345");
        driverRequest.setGender("Male");
        driverRequest.setVehicleNumber("MH12AB1234");
        driverRequest.setVehicleName("Car");
        driverRequest.setCarSeater(5);
        driverRequest.setRole("driver");

        DriverDto driverDto = createMockDriverDto("driver-2", driverRequest.getEmail());

        DriverServiceResponse clientResponse = new DriverServiceResponse(driverDto, "success", "Driver registered successfully");

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(driverClient.registerDriver(any(DriverRequest.class))).thenReturn(new ResponseEntity<>(clientResponse, HttpStatus.CREATED));

        DriverServiceResponse response = authenticationService.registerDriver(driverRequest);

        assertNotNull(response);
        assertEquals("Driver registered successfully", response.getMessage());
        assertEquals("success", response.getStatus());

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("registerDriver_client_not_created_status: should return response body when client status is not CREATED")
    void registerDriver_client_not_created_status() {
        DriverRequest driverRequest = new DriverRequest();
        driverRequest.setFullName("NewDriver");
        driverRequest.setEmail("newDriver@email.com");
        driverRequest.setPhone("1234567890");
        driverRequest.setPassword("pass123");
        driverRequest.setLicenceNumber("DL12345");
        driverRequest.setGender("Male");
        driverRequest.setVehicleNumber("MH12AB1234");
        driverRequest.setVehicleName("Car");
        driverRequest.setCarSeater(5);
        driverRequest.setRole("driver");

        DriverServiceResponse clientResponse = new DriverServiceResponse(null, "error", "Bad Request from Client");

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(driverClient.registerDriver(any(DriverRequest.class))).thenReturn(new ResponseEntity<>(clientResponse, HttpStatus.BAD_REQUEST));

        DriverServiceResponse response = authenticationService.registerDriver(driverRequest);

        assertNotNull(response);
        assertEquals("Bad Request from Client", response.getMessage());

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("registerDriver_client_response_null_body_throws_exception: should throw APIException if client returns CREATED but body/DriverDto is null")
    void registerDriver_client_response_null_body_throws_exception() {
        DriverRequest driverRequest = new DriverRequest();
        driverRequest.setEmail("newDriver@email.com");
        driverRequest.setPhone("1234567890");
        driverRequest.setPassword("pass123");
        driverRequest.setLicenceNumber("DL12345");
        driverRequest.setGender("Male");
        driverRequest.setVehicleNumber("MH12AB1234");
        driverRequest.setVehicleName("Car");
        driverRequest.setCarSeater(5);
        driverRequest.setRole("driver");

       DriverServiceResponse clientResponseWithNullBody = new DriverServiceResponse(null, "success", "Registered but no details");

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(driverClient.registerDriver(any(DriverRequest.class))).thenReturn(new ResponseEntity<>(clientResponseWithNullBody, HttpStatus.CREATED));

        AuthenticationAPIException exception = assertThrows(AuthenticationAPIException.class, () ->
                authenticationService.registerDriver(driverRequest)
        );

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatus());
        assertTrue(exception.getMessage().contains("Driver details not found in response"));
    }

    @Test
    @DisplayName("resetPassword_user_success: should reset password for a user")
    void resetPassword_user_success() {
        ForgotPassword forgotPassword = new ForgotPassword("user@email.com", "newPass");
        User user = new User(1,"user@email.com", "oldEncoded", "user");

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(anyString())).thenReturn("newEncodedPassword");
        when(userClient.forgotPassword(any(ForgotPassword.class))).thenReturn(new ResponseEntity<>(null, HttpStatus.OK));

        PasswordResetResponse response = authenticationService.resetPassword(forgotPassword);

        assertNotNull(response);
        assertEquals("success", response.getStatus());
        assertEquals("User password reset successfully", response.getMessage());
        assertEquals("newEncodedPassword", user.getPassword());

        verify(userRepository, times(2)).findByEmail(anyString());
        verify(userRepository, times(1)).save(any(User.class));
        verify(userClient, times(1)).forgotPassword(any(ForgotPassword.class));
        verify(driverClient, never()).forgotPassword(any(ForgotPassword.class));
    }

    @Test
    @DisplayName("resetPassword_driver_success: should reset password for a driver")
    void resetPassword_driver_success() {
        ForgotPassword forgotPassword = new ForgotPassword("driver@email.com", "newPass");
        User driver = new User(1,"driver@email.com", "oldEncoded", "driver");

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(driver));
        when(passwordEncoder.encode(anyString())).thenReturn("newEncodedPassword");
        when(driverClient.forgotPassword(any(ForgotPassword.class))).thenReturn(new ResponseEntity<>(null, HttpStatus.OK));

        PasswordResetResponse response = authenticationService.resetPassword(forgotPassword);

        assertNotNull(response);
        assertEquals("success", response.getStatus());
        assertEquals("Driver password reset successfully", response.getMessage());
        assertEquals("newEncodedPassword", driver.getPassword());

        verify(userRepository, times(2)).findByEmail(anyString());
        verify(userRepository, times(1)).save(any(User.class));
        verify(driverClient, times(1)).forgotPassword(any(ForgotPassword.class));
        verify(userClient, never()).forgotPassword(any(ForgotPassword.class));
    }

    @Test
    @DisplayName("resetPassword_user_not_found_throws_exception: should throw AuthenticationAPIException if user not found for reset")
    void resetPassword_user_not_found_throws_exception() {
        ForgotPassword forgotPassword = new ForgotPassword("unknown@email.com", "newPass");

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        AuthenticationAPIException exception = assertThrows(AuthenticationAPIException.class, () ->
                authenticationService.resetPassword(forgotPassword)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertTrue(exception.getMessage().contains("User with that email does not exist."));

        verify(passwordEncoder, never()).encode(anyString());
        verify(userClient, never()).forgotPassword(any(ForgotPassword.class));
    }

    @Test
    @DisplayName("resetPassword_client_exception_returns_error_response: should return error status on client failure")
    void resetPassword_client_exception_returns_error_response() {
        ForgotPassword forgotPassword = new ForgotPassword("user@email.com", "newPass");
        User user = new User(1,"user@email.com", "oldEncoded", "user");

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(anyString())).thenReturn("newEncodedPassword");
        when(userClient.forgotPassword(any(ForgotPassword.class))).thenThrow(new RuntimeException("Service Unavailable"));

        PasswordResetResponse response = authenticationService.resetPassword(forgotPassword);

        assertNotNull(response);
        assertEquals("error", response.getStatus());
        assertTrue(response.getMessage().contains("Service Unavailable"));
    }

    @Test
    @DisplayName("deleteUserByEmail_user_success: should delete user from both services")
    void deleteUserByEmail_user_success() {
        String email = "userToDelete@email.com";
        User user = new User(1,email, "pass", "user");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(userClient.deleteUser(email)).thenReturn(new ResponseEntity<>("User deleted from user-service", HttpStatus.OK));

        String result = authenticationService.deleteUserByEmail(email);

        assertEquals("User deleted from user-service", result);

        verify(userClient, times(1)).deleteUser(email);
        verify(userRepository, times(2)).deleteByEmail(email);
        verify(driverClient, never()).deleteDriver(anyString());
    }

    @Test
    @DisplayName("deleteUserByEmail_driver_success: should delete driver from both services")
    void deleteUserByEmail_driver_success() {
        String email = "driverToDelete@email.com";
        User driver = new User(1,email, "pass", "driver");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(driver));
        when(driverClient.deleteDriver(email)).thenReturn(new ResponseEntity<>("Driver deleted from driver-service", HttpStatus.OK));

        String result = authenticationService.deleteUserByEmail(email);

        assertEquals("Driver deleted from driver-service", result);

        verify(driverClient, times(1)).deleteDriver(email);
        verify(userRepository, times(2)).deleteByEmail(email);
        verify(userClient, never()).deleteUser(anyString());
    }

    @Test
    @DisplayName("deleteUserByEmail_user_client_failure: should not delete from local repo if client call fails")
    void deleteUserByEmail_user_client_failure() {
        String email = "userToDelete@email.com";
        User user = new User(1,email, "pass", "user");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(userClient.deleteUser(email)).thenReturn(new ResponseEntity<>("User not found in user-service", HttpStatus.NOT_FOUND));

        String result = authenticationService.deleteUserByEmail(email);

        assertEquals("User not found in user-service", result);

        verify(userRepository, times(0)).deleteByEmail(email);
    }

    @Test
    @DisplayName("deleteUserByEmail_not_found_throws_exception: should throw AuthenticationAPIException if user not found")
    void deleteUserByEmail_not_found_throws_exception() {
        String email = "unknown@email.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        AuthenticationAPIException exception = assertThrows(AuthenticationAPIException.class, () ->
                authenticationService.deleteUserByEmail(email)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertTrue(exception.getMessage().contains("User with that email does not exist."));

        verify(userClient, never()).deleteUser(anyString());
    }

    @Test
    @DisplayName("validateToken_valid: should return true for a valid token")
    void validateToken_valid() {
        String token = "Bearer valid.jwt.token";
        when(jwtTokenProvider.validateToken("valid.jwt.token")).thenReturn(true);

        assertTrue(authenticationService.validateToken(token));
    }

    @Test
    @DisplayName("validateToken_invalid: should return false for an invalid token")
    void validateToken_invalid() {
        String token = "Bearer invalid.jwt.token";
        when(jwtTokenProvider.validateToken("invalid.jwt.token")).thenReturn(false);

        assertFalse(authenticationService.validateToken(token));
    }
}