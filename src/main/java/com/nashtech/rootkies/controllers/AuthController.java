package com.nashtech.rootkies.controllers;

import com.nashtech.rootkies.constants.ErrorCode;
import com.nashtech.rootkies.exception.WrongPasswordOrUsernameException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.nashtech.rootkies.constants.SuccessCode;
import com.nashtech.rootkies.dto.auth.JwtResponse;
import com.nashtech.rootkies.dto.auth.LoginRequest;
import com.nashtech.rootkies.dto.auth.SignUpRequest;
import com.nashtech.rootkies.dto.common.ResponseDTO;
import com.nashtech.rootkies.service.AuthService;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@Tag(name = "AUTH", description = "AUTH API")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Operation(summary = "Login", description = "", tags = {"AUTH"})
    @ApiResponses(value = {@ApiResponse(responseCode = "2xx", description = "Successfull"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")})
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        ResponseEntity<ResponseDTO> response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        try {
            JwtResponse jwtResponse = authService.signIn(loginRequest);
            ResponseDTO dto = new ResponseDTO();
            dto.setData(jwtResponse);
            dto.setSuccessCode(SuccessCode.USER_LOGIN_SUCCESS);

            response = ResponseEntity.ok().body(dto);
        } catch (BadCredentialsException e){
            throw new WrongPasswordOrUsernameException(ErrorCode.WRONG_PASSWORD_OR_USERNAME);
        }
        return response;
    }

    @Operation(summary = "Test call api", description = "", tags = {"AUTH"})
    @ApiResponses(value = {@ApiResponse(responseCode = "2xx", description = "Successfull"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")})
    @GetMapping("/home")
    public String getHome() {
        return "<h1>Home</h1>";
    }

    @Operation(summary = "Fake signup for test", description = "", tags = {"AUTH"})
    @ApiResponses(value = {@ApiResponse(responseCode = "2xx", description = "Successfull"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")})
    @PostMapping("/fakesignup")
    public ResponseEntity<String> fakeSignUp(@RequestBody SignUpRequest request) {
        String message = authService.fakeSignUp(request);
        return ResponseEntity.ok(message);
    }
}
