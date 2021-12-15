package com.nashtech.rootkies.service;

import com.nashtech.rootkies.dto.PageDTO;
import com.nashtech.rootkies.dto.common.ResponseDTO;
import com.nashtech.rootkies.dto.user.UserDetailDTO;
import com.nashtech.rootkies.dto.user.request.ChangePasswordFirstLoginRequest;
import com.nashtech.rootkies.dto.user.request.ChangePasswordRequest;
import com.nashtech.rootkies.dto.user.request.GetUserListDTO;
import com.nashtech.rootkies.exception.*;
import com.nashtech.rootkies.model.User;
import org.hibernate.sql.Update;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Optional;

public interface UserService {

    boolean updateUser(String staffCode, User user) throws UserNotFoundException, ResourceNotFoundException, UpdateDataFailException;

    ResponseDTO changePasswordForFirstLogin (ChangePasswordFirstLoginRequest passwordFirstLoginRequest);
    
    PageDTO getUserList(Pageable pageable, Specification specification) throws DataNotFoundException;

    Optional<User> getUser(String staffCode) throws UserNotFoundException, DataBlockedException;

    boolean createUser(User user) throws CreateDataFailException;

    ResponseDTO changePassword(ChangePasswordRequest request) throws ChangePasswordFailedException, UserNotFoundException;

    boolean disableUser(String staffCode) throws UserNotFoundException, UpdateDataFailException;
    
    UserDetailDTO getUserDetailByStaffCode(String staffCode)  throws UserNotFoundException, StaffCodeNotExistsException, UserIsDeletedException;
}
