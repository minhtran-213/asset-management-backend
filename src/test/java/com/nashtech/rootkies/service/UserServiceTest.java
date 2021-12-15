package com.nashtech.rootkies.service;

import com.nashtech.rootkies.constants.ErrorCode;
import com.nashtech.rootkies.constants.SuccessCode;
import com.nashtech.rootkies.dto.PageDTO;
import com.nashtech.rootkies.dto.common.ResponseDTO;
import com.nashtech.rootkies.dto.user.UserDetailDTO;
import com.nashtech.rootkies.dto.user.request.ChangePasswordFirstLoginRequest;
import com.nashtech.rootkies.dto.user.request.ChangePasswordRequest;
import com.nashtech.rootkies.enums.ERole;
import com.nashtech.rootkies.enums.Gender;
import com.nashtech.rootkies.exception.*;
import com.nashtech.rootkies.dto.user.request.GetUserListDTO;
import com.nashtech.rootkies.exception.InvalidPasswordException;
import com.nashtech.rootkies.exception.ResourceNotFoundException;
import com.nashtech.rootkies.exception.SamePasswordException;
import com.nashtech.rootkies.exception.StaffCodeNotExistsException;
import com.nashtech.rootkies.exception.UserIsDeletedException;
import com.nashtech.rootkies.exception.UserNotFoundException;
import com.nashtech.rootkies.model.Assignment;
import com.nashtech.rootkies.model.Location;
import com.nashtech.rootkies.model.Role;
import com.nashtech.rootkies.model.User;
import com.nashtech.rootkies.repository.AssignmentRepository;
import com.nashtech.rootkies.repository.LocationRepository;
import com.nashtech.rootkies.repository.RoleRepository;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.nashtech.rootkies.repository.UserRepository;

import com.nashtech.rootkies.specification.UserSpecification;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import com.nashtech.rootkies.exception.ChangePasswordFailedException;
import com.nashtech.rootkies.exception.InvalidOldPassword;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceTest {
    @Autowired
    UserService userService;

    @MockBean
    RoleRepository roleRepository;

    @Autowired
    LocationRepository locationRepository;

    @MockBean
    AssignmentRepository assignmentRepository;

    @Autowired
    PasswordEncoder encoder;
    
    @MockBean
    UserRepository userRepository;

    @Test
    public void updateUserTest_success() throws UserNotFoundException, ResourceNotFoundException, UpdateDataFailException {
        User user = new User();
        user.setStaffCode("SD0001");
        user.setFirstName("Nhi");
        user.setLastName("Mai Hoang");
        user.setGender(Gender.Female);
        user.setDateOfBirth(LocalDateTime.of(2000, 6, 1, 0, 0, 0));
        user.setJoinedDate(LocalDateTime.now());
        Role role = Role.builder().id(-1L).roleName(ERole.ROLE_USER).build();
        Mockito.when(roleRepository.findByRoleName(ERole.ROLE_USER)).thenReturn(Optional.of(role));
        user.setRole(role);

        Mockito.when(userRepository.findByStaffCode("SD0001")).thenReturn(Optional.of(user));
        User tempUser = new User();
        tempUser.setGender(Gender.Male);
        tempUser.setRole(role);
        tempUser.setDateOfBirth(LocalDateTime.of(2000, 1, 1, 0, 0));
        tempUser.setJoinedDate(LocalDateTime.now());
        boolean test = userService.updateUser("SD0001", tempUser);

        assertEquals(true, test);
        assertEquals(user.getGender().name(), tempUser.getGender().name());
    }
    @Test
    public void changePasswordForFirstLogin_success () {
        User user = new User();
        user.setUsername("demo");
        user.setPassword(encoder.encode("test12345"));

        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        Mockito.when(authentication.getName()).thenReturn("demo");

        Mockito.when(userRepository.findByUsername("demo")).thenReturn(Optional.of(user));

        ChangePasswordFirstLoginRequest passwordFirstLoginRequest = new ChangePasswordFirstLoginRequest("test1234");
        Mockito.when(userRepository.save(user)).thenReturn(user);

        ResponseDTO responseDTO = userService.changePasswordForFirstLogin(passwordFirstLoginRequest);
        Assertions.assertNotNull(responseDTO);
    }

    @Test
    public void changePasswordForFirstLogin_invalidPassword_throwInvalidPasswordException () {
        User user = new User();
        user.setUsername("demo");
        user.setPassword(encoder.encode("test12345"));

        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        Mockito.when(authentication.getName()).thenReturn("demo");

        Mockito.when(userRepository.findByUsername("demo")).thenReturn(Optional.of(user));

        ChangePasswordFirstLoginRequest passwordFirstLoginRequest = new ChangePasswordFirstLoginRequest("test");
        Mockito.when(userRepository.save(user)).thenReturn(user);

        Exception exception = assertThrows(InvalidPasswordException.class, () -> userService.changePasswordForFirstLogin(passwordFirstLoginRequest));
        assertEquals(exception.getMessage(), ErrorCode.PASSWORD_INVALID);
    }

    @Test
    public void changePasswordForFirstLogin_sameCurrentPassword_throwSameCurrentPasswordException () {
        User user = new User();
        user.setUsername("demo");
        user.setPassword(encoder.encode("test12345"));

        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        Mockito.when(authentication.getName()).thenReturn("demo");

        Mockito.when(userRepository.findByUsername("demo")).thenReturn(Optional.of(user));

        ChangePasswordFirstLoginRequest passwordFirstLoginRequest = new ChangePasswordFirstLoginRequest("test12345");
        Mockito.when(userRepository.save(user)).thenReturn(user);

        Exception exception = assertThrows(SamePasswordException.class, () -> userService.changePasswordForFirstLogin(passwordFirstLoginRequest));
        assertEquals(exception.getMessage(), ErrorCode.SAME_CURRENT_PASSWORD);
    }

    @Test
    public void getUserService_success() throws UserNotFoundException{
       User user = new User();
       user.setStaffCode("SD0001");
       user.setUsername("nhatvv");
       user.setIsDeleted(false);
       user.setIsDisabled(false);

       Mockito.when(userRepository.findByStaffCode("SD0001")).thenReturn(Optional.of(user));

       Optional<User> user1 = userService.getUser("SD0001");
       assertNotNull(user1);
       assertEquals("SD0001", user1.get().getStaffCode());
    }

    @Test
    public void getUser_not_exist_shouldThrowUserNotFoundException() {
        User user = new User();
        user.setStaffCode("SD0001");
        user.setUsername("nhatvv");
        user.setIsDeleted(false);
        user.setIsDisabled(false);

        Mockito.when(userRepository.findByStaffCode("SD0001")).thenReturn(Optional.of(user));

        Exception exception = assertThrows(UserNotFoundException.class, () -> userService.getUser("SD1111"));
        assertEquals(exception.getMessage(), ErrorCode.ERR_USER_NOT_FOUND);
    }

    @Test
    public void getUser_isBlocked_shouldThrowDataBlockedException() {
        User user = new User();
        user.setStaffCode("SD0001");
        user.setUsername("nhatvv");
        user.setIsDisabled(true);

        Mockito.when(userRepository.findByStaffCode("SD0001")).thenReturn(Optional.of(user));

        Exception exception = assertThrows(DataBlockedException.class, () -> userService.getUser("SD0001"));
        assertEquals(exception.getMessage(), ErrorCode.USER_BLOCKED);
    }

    @Test
    public void createUser_success() throws CreateDataFailException{
        User user = new User();
        user.setFirstName("Duy");
        user.setLastName("Vo Van");
        user.setGender(Gender.Male);
        user.setDateOfBirth(LocalDateTime.of(1999, 8, 2, 0 ,0 ,0));
        user.setJoinedDate(LocalDateTime.now());
        Location location = Location.builder().locationId(101L).address("HCM").build();
        user.setLocation(location);
        Role role = Role.builder().id(-1L).roleName(ERole.ROLE_USER).build();
        Mockito.when(roleRepository.findByRoleName(ERole.ROLE_USER)).thenReturn(Optional.of(role));
        user.setRole(role);

        boolean createUser = userService.createUser(user);

        assertEquals(true, createUser);
    }

    @Test
    public void createUser_Under18_shouldThrowCreateDataFailException() throws CreateDataFailException{
        User user = new User();
        user.setFirstName("Duy");
        user.setLastName("Vo Van");
        user.setGender(Gender.Male);
        user.setDateOfBirth(LocalDateTime.of(2010, 8, 2, 0 ,0 ,0));
        user.setJoinedDate(LocalDateTime.now());
        Location location = locationRepository.findByAddress("HCM");
        user.setLocation(location);
        Role role = Role.builder().id(-1L).roleName(ERole.ROLE_USER).build();
        Mockito.when(roleRepository.findByRoleName(ERole.ROLE_USER)).thenReturn(Optional.of(role));
        user.setRole(role);

        Exception exception = assertThrows(CreateDataFailException.class, () -> userService.createUser(user));
        assertEquals(exception.getMessage(), ErrorCode.ERR_CREATE_USER_DOB);
    }

    @Test
    public void createUser_DateOfBirth_IsAfter_JoinedDate_shouldThrowCreateDataFailException() throws CreateDataFailException{
        User user = new User();
        user.setFirstName("Duy");
        user.setLastName("Vo Van");
        user.setGender(Gender.Male);
        user.setDateOfBirth(LocalDateTime.of(2001, 12, 2, 0 ,0 ,0));
        user.setJoinedDate(LocalDateTime.of(2000, 11,11,0,0));
        Location location = locationRepository.findByAddress("HCM");
        user.setLocation(location);
        Role role = Role.builder().id(-1L).roleName(ERole.ROLE_USER).build();
        Mockito.when(roleRepository.findByRoleName(ERole.ROLE_USER)).thenReturn(Optional.of(role));
        user.setRole(role);

        Exception exception = assertThrows(CreateDataFailException.class, () -> userService.createUser(user));
        assertEquals(exception.getMessage(), ErrorCode.ERR_CREATE_USER_JD_DOB);
    }

    @Test
    public void createUser_JoinedDate_Is_Saturday_Or_Sunday_shouldThrowCreateDataFailException() throws CreateDataFailException {
        User user = new User();
        user.setFirstName("Duy");
        user.setLastName("Vo Van");
        user.setGender(Gender.Male);
        user.setDateOfBirth(LocalDateTime.of(2000, 11, 27, 0, 0, 0));
        user.setJoinedDate(LocalDateTime.of(2021,12,5,0,0));
        Location location = locationRepository.findByAddress("HCM");
        user.setLocation(location);
        Role role = Role.builder().id(-1L).roleName(ERole.ROLE_USER).build();
        Mockito.when(roleRepository.findByRoleName(ERole.ROLE_USER)).thenReturn(Optional.of(role));
        user.setRole(role);

        Exception exception = assertThrows(CreateDataFailException.class, () -> userService.createUser(user));
        assertEquals(exception.getMessage(), ErrorCode.ERR_CREATE_USER_JD);
    }

    @Test
    public void changePassword_wrongOldPassword_throwInvalidOldPassword() {
        User user = new User();
        user.setUsername("demo");
        user.setPassword(encoder.encode("test12345"));

        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        Mockito.when(authentication.getName()).thenReturn("demo");

        Mockito.when(userRepository.findByUsername("demo")).thenReturn(Optional.of(user));

        ChangePasswordRequest request = new ChangePasswordRequest("test1234", "test12345");
        Mockito.when(userRepository.save(user)).thenReturn(user);

        Exception exception = assertThrows(InvalidOldPassword.class, () -> userService.changePassword(request));
        assertEquals(exception.getMessage(), ErrorCode.WRONG_OLD_PASSWORD);
    }

	

    @Test
    public void changePassword_invalidPassword_throwInvalidPassword() {
        User user = new User();
        user.setUsername("demo");
        user.setPassword(encoder.encode("test12345"));

        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        Mockito.when(authentication.getName()).thenReturn("demo");

        Mockito.when(userRepository.findByUsername("demo")).thenReturn(Optional.of(user));

        ChangePasswordRequest request = new ChangePasswordRequest("test12345", "test");
        Mockito.when(userRepository.save(user)).thenReturn(user);

        Exception exception = assertThrows(InvalidPasswordException.class, () -> userService.changePassword(request));
        assertEquals(exception.getMessage(), ErrorCode.PASSWORD_INVALID);
    }

    @Test
    public void changePassword_sameCurrentPassword_throwSameCurrentPassword() {
        User user = new User();
        user.setUsername("demo");
        user.setPassword(encoder.encode("test12345"));

        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        Mockito.when(authentication.getName()).thenReturn("demo");

        Mockito.when(userRepository.findByUsername("demo")).thenReturn(Optional.of(user));

        ChangePasswordRequest request = new ChangePasswordRequest("test12345", "test12345");
        Mockito.when(userRepository.save(user)).thenReturn(user);

        Exception exception = assertThrows(SamePasswordException.class, () -> userService.changePassword(request));
        assertEquals(exception.getMessage(), ErrorCode.SAME_CURRENT_PASSWORD);
    }

    @Test
    public void changePassword_success_returnResponseDTO() throws UserNotFoundException {
        User user = new User();
        user.setUsername("demo");
        user.setPassword(encoder.encode("test12345"));

        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        Mockito.when(authentication.getName()).thenReturn("demo");

        Mockito.when(userRepository.findByUsername("demo")).thenReturn(Optional.of(user));

        ChangePasswordRequest request = new ChangePasswordRequest("test12345", "test123456");
        Mockito.when(userRepository.save(user)).thenReturn(user);

        ResponseDTO responseDTO = null;
        try {
            responseDTO = userService.changePassword(request);
        } catch (ChangePasswordFailedException e) {
            e.printStackTrace();
        }
        assertNotNull(responseDTO);
        assertEquals(SuccessCode.CHANGE_PASSWORD_SUCCESS, responseDTO.getSuccessCode());
        assertEquals(true, responseDTO.getData());
    }

    @Test
    public void disableUser_success() throws UpdateDataFailException{
        User user = new User();
        user.setStaffCode("SD0001");
        user.setIsDisabled(false);

        Mockito.when(userRepository.findByStaffCode("SD0001")).thenReturn(Optional.of(user));

        boolean disableUser = userService.disableUser("SD0001");
        assertEquals(true, disableUser);
    }

    @Test
    public void disableUser_notFoundByStaffCode_shouldThrowUserNotFoundException() throws UpdateDataFailException{
        User user = new User();
        user.setStaffCode("SD0001");
        user.setIsDisabled(false);

        Mockito.when(userRepository.findByStaffCode("SD0001")).thenReturn(Optional.of(user));

        Exception exception = assertThrows(UserNotFoundException.class, () -> userService.disableUser("KK0001"));
        assertEquals(exception.getMessage(), ErrorCode.ERR_USER_NOT_FOUND);
    }

    @Test
    public void disableUser_haveValidAssignment_shouldThrowUpdateDataFailException() throws UpdateDataFailException {
        User user = new User();
        user.setStaffCode("SD0001");
        user.setIsDisabled(false);
        Mockito.when(userRepository.findByStaffCode("SD0001")).thenReturn(Optional.of(user));

        Assignment assignment = new Assignment();
        assignment.setAssignmentId(1L);
        assignment.setAssignedTo(user);

        Mockito.when(assignmentRepository.checkUserHasValidAssignment("SD0001")).thenReturn(true);

        Exception exception = assertThrows(UserHaveValidAssignmentException.class, () -> userService.disableUser("SD0001"));
        assertEquals(exception.getMessage(), ErrorCode.ERR_USER_HAVE_VALID_ASSIGNMENT);
    }

	@Test
	public void getUserDetailByStaffCode_success() throws UserNotFoundException, StaffCodeNotExistsException, UserIsDeletedException {
		User user = new User();
		user.setStaffCode("SD0001");
	    user.setFirstName("Nhi");
	    user.setLastName("Mai Hoang");
	    user.setUsername("nhimh");
	    user.setGender(Gender.Female);
	    user.setDateOfBirth(LocalDateTime.of(2000, 6, 1, 0, 0, 0));
	    user.setJoinedDate(LocalDateTime.now());
	    user.setRole(new Role(1001L, ERole.ROLE_USER, null));
	    user.setLocation(new Location(101L, "HCM", null, null));
	    user.setIsDeleted(false);
	    
	    Mockito.when(userRepository.existsByStaffCode(any(String.class))).thenReturn(true);
	    Mockito.when(userRepository.findByStaffCode(any(String.class))).thenReturn(Optional.of(user));
	    
	    UserDetailDTO userDetailDTO = userService.getUserDetailByStaffCode("SD0001");
	    Assertions.assertNotNull(userDetailDTO);
	    assertEquals(user.getStaffCode(), userDetailDTO.getStaffCode());
	    assertEquals(user.getFirstName() + " " + user.getLastName(), userDetailDTO.getFullName());
	    assertEquals(user.getUsername(), userDetailDTO.getUsername());
	    assertEquals(user.getDateOfBirth().toLocalDate(), userDetailDTO.getDateOfBirth());
	    assertEquals(user.getJoinedDate().toLocalDate(), userDetailDTO.getJoinedDate());
	    assertEquals(user.getRole().getRoleName().toString(), userDetailDTO.getType());
	    assertEquals(user.getLocation().getAddress(), userDetailDTO.getLocation());
	}

	@Test
	public void getUserDetailByStaffCode_staffCodeNotExists_throwStaffCodeNotExistsException() {
		Mockito.when(userRepository.existsByStaffCode(any(String.class))).thenReturn(false);
		Exception exception = assertThrows(StaffCodeNotExistsException.class, () -> userService.getUserDetailByStaffCode("SD0001"));
		
		String expectedMessage = ErrorCode.ERR_STAFF_CODE_NOT_EXISTS;
	    String actualMessage = exception.getMessage();

	    assertTrue(actualMessage.contains(expectedMessage));
	}

	@Test
	public void getUserDetailByStaffCode_userNotFound_throwUserNotFoundException() {
		Mockito.when(userRepository.existsByStaffCode(any(String.class))).thenReturn(true);
		Mockito.when(userRepository.findByStaffCode(any(String.class))).thenReturn(Optional.ofNullable(null));
		Exception exception = assertThrows(UserNotFoundException.class, () -> userService.getUserDetailByStaffCode("SD0001"));
		
		String expectedMessage = ErrorCode.ERR_USER_NOT_FOUND;
	    String actualMessage = exception.getMessage();

	    assertTrue(actualMessage.contains(expectedMessage));
	}

	@Test
	public void getUserDetailByStaffCode_userIsDeleted_throwUserIsDeletedException() {
		User user = new User();
	    user.setIsDeleted(true);
	    
	    Mockito.when(userRepository.existsByStaffCode(any(String.class))).thenReturn(true);
		Mockito.when(userRepository.findByStaffCode(any(String.class))).thenReturn(Optional.of(user));
		Exception exception = assertThrows(UserIsDeletedException.class, () -> userService.getUserDetailByStaffCode("SD0001"));
		
		String expectedMessage = ErrorCode.USER_IS_DISABLED;
	    String actualMessage = exception.getMessage();

	    assertTrue(actualMessage.contains(expectedMessage));
	}
}
