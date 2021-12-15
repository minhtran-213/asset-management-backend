package com.nashtech.rootkies.converter;

import com.nashtech.rootkies.constants.ErrorCode;
import com.nashtech.rootkies.dto.PageDTO;
import com.nashtech.rootkies.dto.user.ManageUserResponse;
import com.nashtech.rootkies.dto.user.UserDTO;
import com.nashtech.rootkies.dto.user.UserDetailDTO;
import com.nashtech.rootkies.dto.user.request.CreateUserDTO;
import com.nashtech.rootkies.dto.user.request.EditUserDTO;
import com.nashtech.rootkies.enums.ERole;
import com.nashtech.rootkies.enums.Gender;
import com.nashtech.rootkies.exception.ConvertEntityDTOException;
import com.nashtech.rootkies.exception.ConverterUserException;
import com.nashtech.rootkies.model.Location;
import com.nashtech.rootkies.model.User;
import com.nashtech.rootkies.repository.LocationRepository;
import com.nashtech.rootkies.repository.RoleRepository;
import org.apache.tomcat.jni.Local;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Component
public class UserConverter {
    @Autowired
    ModelMapper modelMapper;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    LocationRepository locationRepository;

    public UserDetailDTO entityToDetailDTO(User user) throws ConverterUserException {
        try {
            return UserDetailDTO.builder()
                    .staffCode(user.getStaffCode())
                    .fullName(user.getFirstName() + " " + user.getLastName())
                    .username(user.getUsername())
                    .joinedDate(user.getJoinedDate().toLocalDate())
                    .dateOfBirth(user.getDateOfBirth().toLocalDate())
                    .gender(user.getGender().toString())
                    .type(user.getRole().getRoleName().toString())
                    .location(user.getLocation().getAddress())
                    .build();
        } catch (Exception e) {
            throw new ConverterUserException(ErrorCode.ERR_CONVERT_ENTITY_DTO_FAIL);
        }
    }

    public ManageUserResponse entityToManageUserResponse (User user) {
        String type;
        if (user.getRole().getRoleName().toString().equals("ROLE_ADMIN")){
            type = "Admin";
        } else {
            type= "Staff";
        }

        return ManageUserResponse.builder()
                .staffCode(user.getStaffCode())
                .fullName(user.getFirstName() + " " + user.getLastName())
                .username(user.getUsername())
                .joinedDate(user.getJoinedDate().toLocalDate())
                .type(type)
                .build();
    }

    public User convertEditUserDTOtoEntity(EditUserDTO editUserDTO) throws ConvertEntityDTOException {
        try {
            User user = modelMapper.map(editUserDTO, User.class);
            user.setGender(Gender.valueOf(editUserDTO.getGender()));

            ERole role = ERole.valueOf(editUserDTO.getRole());
            user.setRole(roleRepository.findByRoleName(role).get());

            Location location = locationRepository.findByLocationId(editUserDTO.getLocation());
            user.setLocation(location);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            user.setDateOfBirth(LocalDateTime.parse(editUserDTO.getDateOfBirth(), formatter));
            user.setJoinedDate(LocalDateTime.parse(editUserDTO.getJoinedDate(), formatter));
            return user;
        } catch (Exception e) {
            throw new ConvertEntityDTOException(ErrorCode.ERR_CONVERT_DTO_ENTITY_FAIL);
        }
    }

    public UserDTO convertUserToDTO(User user) {
        UserDTO userDTO = modelMapper.map(user, UserDTO.class);

        userDTO.setGender(user.getGender().name());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        userDTO.setDateOfBirth(user.getDateOfBirth().format(formatter));
        userDTO.setJoinedDate(user.getJoinedDate().format(formatter));

        userDTO.setRole(user.getRole().getRoleName().name());

        return userDTO;
    }

    public User convertCreateUserDTOtoEntity(CreateUserDTO createUserDTO) throws ConvertEntityDTOException{
        try {
            User user = modelMapper.map(createUserDTO, User.class);

            user.setGender(Gender.valueOf(createUserDTO.getGender()));

            ERole eRole = ERole.valueOf(createUserDTO.getRole());
            user.setRole(roleRepository.findByRoleName(eRole).get());

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            user.setDateOfBirth(LocalDateTime.parse(createUserDTO.getDateOfBirth(), formatter));
            user.setJoinedDate(LocalDateTime.parse(createUserDTO.getJoinedDate(), formatter));

            Location location = locationRepository.findByLocationId(createUserDTO.getLocation());
            user.setLocation(location);
            return user;
        } catch (Exception e) {
            throw new ConvertEntityDTOException(ErrorCode.ERR_CONVERT_DTO_ENTITY_FAIL);
        }
    }



    public PageDTO pageToPageDTO (Page<User> page){
        List<ManageUserResponse> users = page.stream()
                .map(this::entityToManageUserResponse)
                .collect(Collectors.toList());

        return PageDTO.builder().totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .data(users)
                .build();
    }
}
