package com.nashtech.rootkies.service.impl;

import com.nashtech.rootkies.constants.ErrorCode;
import com.nashtech.rootkies.constants.SuccessCode;
import com.nashtech.rootkies.converter.UserConverter;
import com.nashtech.rootkies.dto.common.ResponseDTO;
import com.nashtech.rootkies.dto.user.request.ChangePasswordFirstLoginRequest;
import com.nashtech.rootkies.exception.*;
import com.nashtech.rootkies.dto.user.request.ChangePasswordRequest;
import com.nashtech.rootkies.exception.InvalidPasswordException;
import com.nashtech.rootkies.exception.SamePasswordException;
import com.nashtech.rootkies.exception.StaffCodeNotExistsException;
import com.nashtech.rootkies.exception.UserIsDeletedException;
import com.nashtech.rootkies.model.Assignment;
import com.nashtech.rootkies.model.User;
import com.nashtech.rootkies.dto.PageDTO;
import com.nashtech.rootkies.dto.user.UserDetailDTO;
import com.nashtech.rootkies.model.Role;
import com.nashtech.rootkies.repository.AssignmentRepository;
import com.nashtech.rootkies.repository.RoleRepository;
import com.nashtech.rootkies.repository.UserRepository;
import com.nashtech.rootkies.service.AuthService;
import com.nashtech.rootkies.service.UserService;
import com.nashtech.rootkies.specification.UserSpecification;
import com.nashtech.rootkies.utils.Validation;

import lombok.AllArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.Optional;

import org.hibernate.sql.Update;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;


@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private final UserConverter converter;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    private AuthService authService;

    @Override
    public ResponseDTO changePasswordForFirstLogin(ChangePasswordFirstLoginRequest passwordFirstLoginRequest) {
        User user = userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new RuntimeException("User is not logged in"));
        boolean validPassword = Validation.validatePassword(passwordFirstLoginRequest.getNewPassword());
        boolean samePassword = BCrypt.checkpw(passwordFirstLoginRequest.getNewPassword(), user.getPassword());
        //
        if (!validPassword){
            throw new InvalidPasswordException(ErrorCode.PASSWORD_INVALID);
        } else if (samePassword) {
            throw new SamePasswordException(ErrorCode.SAME_CURRENT_PASSWORD);
        } else {
            String hashPass = encoder.encode(passwordFirstLoginRequest.getNewPassword());
            user.setPassword(hashPass);
            user.setFirstLogin(false);
            userRepository.save(user);
        }
        return new ResponseDTO(null, true, SuccessCode.CHANGE_PASSWORD_SUCCESS);
    }

    @Override
    public boolean updateUser(String staffCode, User user) throws UserNotFoundException, ResourceNotFoundException, UpdateDataFailException {
        User userExist = userRepository.findByStaffCode(staffCode).orElseThrow(() ->
                new UserNotFoundException(ErrorCode.ERR_USER_NOT_FOUND));
        Role roleExist = roleRepository.findByRoleName(user.getRole().getRoleName()).orElseThrow(() ->
                new ResourceNotFoundException(ErrorCode.ERR_ROLE_NOT_FOUND));
        if (user.getDateOfBirth().until(LocalDateTime.now(), ChronoUnit.YEARS) < 18) {
            LOGGER.error("User is under 18 years old");
            throw new UpdateDataFailException(ErrorCode.ERR_CREATE_USER_DOB);
        }
        if (user.getDateOfBirth().isAfter(user.getJoinedDate())) {
            LOGGER.error("Joined date must after date of birth");
            throw new UpdateDataFailException(ErrorCode.ERR_CREATE_USER_JD_DOB);
        }
        DayOfWeek day = user.getJoinedDate().getDayOfWeek();
        if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
            LOGGER.error("Joined date must not be Saturday or Sunday");
            throw new UpdateDataFailException(ErrorCode.ERR_CREATE_USER_JD);
        }

        try {
            userExist.setDateOfBirth(user.getDateOfBirth());
            userExist.setJoinedDate(user.getJoinedDate());
            userExist.setGender(user.getGender());
            userExist.setRole(roleExist);

            userRepository.save(userExist);
            return true;
        } catch (Exception e) {
            throw new UpdateDataFailException(e.getMessage());
        }


    }


	@Override
	public PageDTO getUserList(Pageable pageable, Specification specification) throws DataNotFoundException {
        try {
            Page<User> page = userRepository.findAll(specification, pageable);
            PageDTO pageDTO = converter.pageToPageDTO(page);
            return pageDTO;
        } catch (Exception e){
            throw new DataNotFoundException(ErrorCode.ERR_GET_ALL_USER);
        }
	}

    @Override
    public Optional<User> getUser(String staffCode) throws UserNotFoundException, DataBlockedException {
        User user = userRepository.findByStaffCode(staffCode).orElseThrow(
                () -> new UserNotFoundException(ErrorCode.ERR_USER_NOT_FOUND));

        if(user.getIsDisabled() || user.getIsDeleted()) {
            throw new DataBlockedException(ErrorCode.USER_BLOCKED);
        }
        return Optional.of(user);
    }

    @Override
    public ResponseDTO changePassword(ChangePasswordRequest request) throws UserNotFoundException, ChangePasswordFailedException {
        Optional<User> userOptional = userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        if (userOptional.isEmpty()){
            throw new UserNotFoundException(ErrorCode.USER_NOT_FOUND);
        } else {
            User user = userOptional.get();
            // check old password
            if (!BCrypt.checkpw(request.getOldPassword(), user.getPassword())){
                throw new InvalidOldPassword(ErrorCode.WRONG_OLD_PASSWORD);
            }
            // check valid new password
            boolean validPassword = Validation.validatePassword(request.getNewPassword());
            boolean samePassword = BCrypt.checkpw(request.getNewPassword(), user.getPassword());
            if (!validPassword){
                throw new InvalidPasswordException(ErrorCode.PASSWORD_INVALID);
            } else if (samePassword){
                throw new SamePasswordException(ErrorCode.SAME_CURRENT_PASSWORD);
            } else {
                String hashPass = encoder.encode(request.getNewPassword());
                user.setPassword(hashPass);
                try {
                    userRepository.save(user);

                } catch (Exception e){
                    throw new ChangePasswordFailedException(ErrorCode.CHANGE_PASSWORD_FAILED);
                }
            }
            return new ResponseDTO(null, true, SuccessCode.CHANGE_PASSWORD_SUCCESS);
        }
    }

    @Override
    public boolean disableUser(String staffCode) throws UserNotFoundException, UpdateDataFailException {
        Optional<User> optionalUser = userRepository.findByStaffCode(staffCode);
        if(optionalUser.isEmpty()) {
            throw new UserNotFoundException(ErrorCode.ERR_USER_NOT_FOUND);
        }
        User user = optionalUser.get();
        boolean checkUserHasValidAssignment = assignmentRepository.checkUserHasValidAssignment(user.getStaffCode());
        if(checkUserHasValidAssignment) {
            throw new UserHaveValidAssignmentException(ErrorCode.ERR_USER_HAVE_VALID_ASSIGNMENT);
        }
        try {
            user.setIsDisabled(true);
            userRepository.save(user);
            return true;
        } catch (Exception e) {
            throw new UpdateDataFailException(e.getMessage());
        }
    }

    @Override
    public boolean createUser(User user) throws CreateDataFailException {
        try {
            if (user.getDateOfBirth().until(LocalDateTime.now(), ChronoUnit.YEARS) < 18) {
                LOGGER.error("User is under 18 years old");
                throw new CreateDataFailException(ErrorCode.ERR_CREATE_USER_DOB);
            }
            if (user.getDateOfBirth().isAfter(user.getJoinedDate())) {
                LOGGER.error("Joined date must after date of birth");
                throw new CreateDataFailException(ErrorCode.ERR_CREATE_USER_JD_DOB);
            }
            DayOfWeek day = user.getJoinedDate().getDayOfWeek();
            if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
                LOGGER.error("Joined date must not be Saturday or Sunday");
                throw new CreateDataFailException(ErrorCode.ERR_CREATE_USER_JD);
            }

            String username = user.getFirstName().toLowerCase(Locale.ROOT);
            String[] lastnameWords = user.getLastName().split(" ");
            for (String word : lastnameWords) {
                username += word.toLowerCase(Locale.ROOT).charAt(0);
            }

            user.setUsername(username);
            int i = 1;
            while (userRepository.existsByUsername(user.getUsername())) {
                user.setUsername(username + i);
                i++;
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
            String password = user.getUsername() + '@' + user.getDateOfBirth().format(formatter);
            user.setPassword(encoder.encode(password));
            user.setIsDeleted(false);
            user.setIsDisabled(false);
            user.setFirstLogin(true);

            userRepository.save(user);
            return true;
        } catch (CreateDataFailException e) {
            throw new CreateDataFailException(e.getMessage());
        }
    }
	
	@Override
	public UserDetailDTO getUserDetailByStaffCode(String staffCode) throws UserNotFoundException, StaffCodeNotExistsException, UserIsDeletedException {
		if (!userRepository.existsByStaffCode(staffCode)) {
			throw new StaffCodeNotExistsException(ErrorCode.ERR_STAFF_CODE_NOT_EXISTS);
		}
		
		User user = userRepository.findByStaffCode(staffCode).orElseThrow(() ->
        	new UserNotFoundException(ErrorCode.ERR_USER_NOT_FOUND));
		
		if (user.getIsDeleted()) {
			throw new UserIsDeletedException(ErrorCode.USER_IS_DISABLED);
		}
		return converter.entityToDetailDTO(user);
	}
}
