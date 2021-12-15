package com.nashtech.rootkies.service;


import com.nashtech.rootkies.constants.ErrorCode;

import com.nashtech.rootkies.constants.ErrorCode;

import com.nashtech.rootkies.constants.ErrorCode;
import com.nashtech.rootkies.dto.auth.JwtResponse;
import com.nashtech.rootkies.dto.auth.LoginRequest;

import com.nashtech.rootkies.enums.ERole;
import com.nashtech.rootkies.enums.Gender;

import com.nashtech.rootkies.exception.custom.ApiRequestException;

import com.nashtech.rootkies.model.Location;
import com.nashtech.rootkies.model.Role;
import com.nashtech.rootkies.model.User;
import com.nashtech.rootkies.security.jwt.JwtUtils;
import com.nashtech.rootkies.security.service.UserDetailsImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.security.core.Authentication;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AuthServiceTest {

    @Autowired
    AuthService authService;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @MockBean
    AuthenticationManager authenticationManager;

    @Test
    public void signIn_success() {
        User user = new User();
        user.setStaffCode("LA00001222");
        user.setUsername("demo");
        user.setPassword(encoder.encode("test12345"));
        user.setFirstName("minh");
        user.setLastName("tran");
        user.setDateOfBirth(LocalDateTime.now());
        user.setJoinedDate(LocalDateTime.now());
        user.setGender(Gender.Male);
        user.setLocation(new Location());
        user.setFirstLogin(true);
        user.setIsDeleted(false);

        Role userRole = new Role();
        userRole.setId((long) -1);
        userRole.setRoleName(ERole.ROLE_USER);

        user.setRole(userRole);

        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authenticationManager.authenticate(Mockito.any())).thenReturn(authentication);
        UserDetailsImpl userDetails = UserDetailsImpl.build(user);
        Mockito.when(authentication.getPrincipal()).thenReturn(userDetails);

        JwtResponse jwtResponse = authService.signIn(new LoginRequest("demo", "test12345"));
        assertNotNull(jwtResponse);
        assertNotNull(jwtResponse.getToken());
    }

    @Test
    public void signIn_wrongUsernameOrPassword() {
        User user = new User();
        user.setStaffCode("LA00001222");
        user.setUsername("demo");
        user.setPassword(encoder.encode("test12345"));
        user.setFirstName("minh");
        user.setLastName("tran");
        user.setDateOfBirth(LocalDateTime.now());
        user.setJoinedDate(LocalDateTime.now());
        user.setGender(Gender.Male);
        user.setLocation(new Location());
        user.setFirstLogin(true);
        user.setIsDeleted(false);

        Role userRole = new Role();
        userRole.setId((long) -1);
        userRole.setRoleName(ERole.ROLE_USER);

        user.setRole(userRole);

        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authenticationManager.authenticate(Mockito.any())).thenThrow(new BadCredentialsException("Bad credential"));
        UserDetailsImpl userDetails = UserDetailsImpl.build(user);
        Mockito.when(authentication.getPrincipal()).thenReturn(userDetails);

        Exception exception = assertThrows(AuthenticationException.class,
                () -> authService.signIn(new LoginRequest("demo", "test123")));
        assertEquals("Bad credential", exception.getMessage());
        assertEquals(exception.getClass(), BadCredentialsException.class);
    }

    @Test
    public void signIn_userIsBlocked() {
        User user = new User();
        user.setStaffCode("LA00001222");
        user.setUsername("demo");
        user.setPassword(encoder.encode("test12345"));
        user.setFirstName("minh");
        user.setLastName("tran");
        user.setDateOfBirth(LocalDateTime.now());
        user.setJoinedDate(LocalDateTime.now());
        user.setGender(Gender.Male);
        user.setLocation(new Location());
        user.setFirstLogin(true);
        user.setIsDeleted(false);

        Role userRole = new Role();
        userRole.setId((long) -1);
        userRole.setRoleName(ERole.ROLE_USER_LOCKED);

        user.setRole(userRole);

        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authenticationManager.authenticate(Mockito.any())).thenReturn(authentication);
        UserDetailsImpl userDetails = UserDetailsImpl.build(user);
        Mockito.when(authentication.getPrincipal()).thenReturn(userDetails);

        Exception exception = assertThrows(ApiRequestException.class,
                () -> authService.signIn(new LoginRequest("demo", "test12345")));
        assertEquals(ErrorCode.USER_BLOCKED, exception.getMessage());
        assertEquals(exception.getClass(), ApiRequestException.class);
    }

}