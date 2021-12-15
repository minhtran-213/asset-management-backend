package com.nashtech.rootkies.controllers;

import com.nashtech.rootkies.dto.common.ResponseDTO;
import com.nashtech.rootkies.dto.user.request.ChangePasswordFirstLoginRequest;
import com.nashtech.rootkies.dto.user.request.CreateUserDTO;
import com.nashtech.rootkies.dto.user.request.ChangePasswordRequest;
import com.nashtech.rootkies.exception.*;
import com.nashtech.rootkies.exception.InvalidPasswordException;
import com.nashtech.rootkies.exception.SamePasswordException;
import com.nashtech.rootkies.exception.StaffCodeNotExistsException;
import com.nashtech.rootkies.constants.ErrorCode;
import com.nashtech.rootkies.constants.SuccessCode;
import com.nashtech.rootkies.dto.user.request.EditUserDTO;
import com.nashtech.rootkies.exception.UpdateDataFailException;
import com.nashtech.rootkies.exception.UserIsDeletedException;
import com.nashtech.rootkies.exception.UserNotFoundException;
import com.nashtech.rootkies.model.User;
import com.nashtech.rootkies.specification.UserSpecificationBuilder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;
import com.nashtech.rootkies.converter.UserConverter;
import com.nashtech.rootkies.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.validation.constraints.NotBlank;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/user")
@Tag(name = "USER", description = "USER API")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    UserConverter userConverter;

    @Operation(summary = "Test call api", description = "", tags = { "USER" }, security = {
         @SecurityRequirement(name = "bearer-key-user") })
    @ApiResponses(value = { @ApiResponse(responseCode = "2xx", description = "Successfull"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error") })
    @GetMapping("/home")
    @PreAuthorize("hasRole('USER')")
    public String getHome() {
        return "<h1>USER Home Page</h1>";
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @Operation(summary = "Edit user", description = "", tags = { "USER" }, security = {
            @SecurityRequirement(name = "bearer-key-user") })
    @ApiResponses(value = { @ApiResponse(responseCode = "2xx", description = "Successfull"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error") })
    @PutMapping("/edit/{staffcode}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO> updateUser(@PathVariable(value = "staffcode") String staffcode,
                                                  @Valid @RequestBody EditUserDTO editUserDTO) throws UpdateDataFailException {
        ResponseDTO responseDTO = new ResponseDTO();
        try {
            User user = userConverter.convertEditUserDTOtoEntity(editUserDTO);
            boolean updateUser = userService.updateUser(staffcode, user);
            responseDTO.setData(updateUser);
            responseDTO.setSuccessCode(SuccessCode.USER_UPDATED_SUCCESS);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            throw new UpdateDataFailException(e.getMessage());
        }
        return ResponseEntity.ok(responseDTO);
    }

    @PutMapping("/password")
    public ResponseEntity<ResponseDTO> changePasswordFirstTimeLogin (@RequestBody @Valid ChangePasswordFirstLoginRequest newPassword){
        ResponseEntity<ResponseDTO> response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        try {
            ResponseDTO responseDTO = userService.changePasswordForFirstLogin(newPassword);
            response = ResponseEntity.ok().body(responseDTO);
        } catch (InvalidPasswordException e) {
            LOGGER.error(ErrorCode.PASSWORD_INVALID);
            throw new InvalidPasswordException(ErrorCode.PASSWORD_INVALID);
        } catch (SamePasswordException e) {
            LOGGER.error(ErrorCode.SAME_CURRENT_PASSWORD);
            throw new SamePasswordException(ErrorCode.SAME_CURRENT_PASSWORD);
        }
        return response;
    }


    @Operation(summary = "Get user list", description = "", tags = { "USER" }, security = {
            @SecurityRequirement(name = "bearer-key-user") })
    @ApiResponses(value = { @ApiResponse(responseCode = "2xx", description = "Successfull"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error") })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO> getAllUser(@RequestParam Integer page,
                                                  @RequestParam Integer size,
                                                  @RequestParam String sort,
                                                  @RequestParam String search)throws DataNotFoundException {
        ResponseDTO response = new ResponseDTO();
        try {
            Pageable pageable = null;
            if (sort.contains("ASC")) {
                pageable = PageRequest.of(page, size, Sort.by(sort.replace("ASC", "")).ascending());
            } else {
                pageable = PageRequest.of(page, size, Sort.by(sort.replace("DES", "")).descending());
            }


            UserSpecificationBuilder builder = new UserSpecificationBuilder();
            Pattern pattern = Pattern.compile("(\\w+?)(:|<|>|@)(\\w+?),");
            Matcher matcher = pattern.matcher(search + ",");
            while (matcher.find()) {
                builder.with(matcher.group(1), matcher.group(2), matcher.group(3));
            }

            Specification<User> spec = builder.build();

            response.setData(userService.getUserList(pageable, spec));

            response.setSuccessCode(SuccessCode.GET_USER_SUCCESS);
            return ResponseEntity.ok().body(response);
        } catch (Exception ex) {
            response.setErrorCode(ErrorCode.GET_USER_FAIL);
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/logout")
    public ResponseEntity<ResponseDTO> logout(HttpServletRequest request, HttpServletResponse response){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        ResponseDTO responseDTO = new ResponseDTO(null, true, SuccessCode.LOGOUT_SUCCESS );
        return ResponseEntity.ok().body(responseDTO);
    }
    
    @Operation(summary = "Get user detail", description = "", tags = { "USER" }, security = {
            @SecurityRequirement(name = "bearer-key-user") })
    @ApiResponses(value = { @ApiResponse(responseCode = "2xx", description = "Successfull"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error") })
    @GetMapping("/detail/{staffcode}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO> getUserDetail(@PathVariable(value = "staffcode") @NotBlank String staffCode) throws UserNotFoundException, StaffCodeNotExistsException, UserIsDeletedException {
    	ResponseDTO responseDTO = new ResponseDTO();
    	try {
    		responseDTO.setData(userService.getUserDetailByStaffCode(staffCode));
    		responseDTO.setSuccessCode(SuccessCode.GET_USER_SUCCESS);
    	} catch (StaffCodeNotExistsException e) {
			LOGGER.error(ErrorCode.ERR_STAFF_CODE_NOT_EXISTS);
			throw new StaffCodeNotExistsException(ErrorCode.ERR_STAFF_CODE_NOT_EXISTS);
		} catch (UserIsDeletedException e) {
			LOGGER.error(ErrorCode.USER_IS_DISABLED);
			throw new UserIsDeletedException(ErrorCode.USER_IS_DISABLED);
		} catch (UserNotFoundException e) {
			LOGGER.error(ErrorCode.ERR_USER_NOT_FOUND);
			throw new UserNotFoundException(ErrorCode.ERR_USER_NOT_FOUND);
		}
    	return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/{staffcode}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO> getUser(@PathVariable("staffcode") String staffCode) throws UserNotFoundException {
        ResponseDTO responseDTO = new ResponseDTO();
        try {
            Optional<User> user = userService.getUser(staffCode);
            responseDTO.setData(userConverter.convertUserToDTO(user.get()));
            responseDTO.setSuccessCode(SuccessCode.FIND_USER_SUCCESS);
        } catch (Exception e) {
            LOGGER.error(ErrorCode.GET_USER_FAIL);
            throw new UserNotFoundException(ErrorCode.ERR_USER_NOT_FOUND);
        }
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping("/save")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO> createUser(@Valid @RequestBody CreateUserDTO createUserDTO) throws CreateDataFailException, ConvertEntityDTOException {
        ResponseDTO responseDTO = new ResponseDTO();
        try {
            User user = userConverter.convertCreateUserDTOtoEntity(createUserDTO);
            boolean createUser = userService.createUser(user);
            responseDTO.setData(createUser);
            responseDTO.setSuccessCode(SuccessCode.USER_CREATED_SUCCESS);
        } catch (CreateDataFailException e) {
            LOGGER.error(e.getMessage());
            throw new CreateDataFailException(e.getMessage());
        } catch (ConvertEntityDTOException e) {
            throw new ConvertEntityDTOException(ErrorCode.ERR_CONVERT_DTO_ENTITY_FAIL);
        }
        return ResponseEntity.ok(responseDTO);
    }

    @PutMapping("/passwordChange")
    public ResponseEntity<ResponseDTO> changePassword(@RequestBody @Valid ChangePasswordRequest request) throws UserNotFoundException, ChangePasswordFailedException {
        ResponseEntity<ResponseDTO> response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        try {
            ResponseDTO responseDTO = userService.changePassword(request);
            response = ResponseEntity.ok().body(responseDTO);
        } catch (InvalidPasswordException e) {
            LOGGER.error(ErrorCode.PASSWORD_INVALID);
            throw new InvalidPasswordException(ErrorCode.PASSWORD_INVALID);
        } catch (SamePasswordException e) {
            LOGGER.error(ErrorCode.SAME_CURRENT_PASSWORD);
            throw new SamePasswordException(ErrorCode.SAME_CURRENT_PASSWORD);
        } catch (InvalidOldPassword e){
            LOGGER.error(ErrorCode.WRONG_OLD_PASSWORD);
            throw new InvalidPasswordException(ErrorCode.WRONG_OLD_PASSWORD);
        } catch (ChangePasswordFailedException e) {
            LOGGER.error(ErrorCode.CHANGE_PASSWORD_FAILED);
            throw new ChangePasswordFailedException(ErrorCode.CHANGE_PASSWORD_FAILED);
        }
        LOGGER.info(SuccessCode.CHANGE_PASSWORD_SUCCESS);
        return response;
    }

    @PutMapping("/disable/{staffcode}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO> disableUser(@PathVariable("staffcode") String staffCode) throws UserNotFoundException, UpdateDataFailException {
        ResponseDTO responseDTO = new ResponseDTO();
        try {
            boolean disabledUser = userService.disableUser(staffCode);
            responseDTO.setData(disabledUser);
            responseDTO.setSuccessCode(SuccessCode.DISABLE_USER_SUCCESS);
        } catch (UpdateDataFailException e) {
            LOGGER.error(e.getMessage());
            throw new UpdateDataFailException(e.getMessage());
        }
        LOGGER.info(SuccessCode.DISABLE_USER_SUCCESS);
        return ResponseEntity.ok(responseDTO);
    }

}
