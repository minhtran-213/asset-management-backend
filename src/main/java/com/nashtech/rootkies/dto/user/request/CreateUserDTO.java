package com.nashtech.rootkies.dto.user.request;

import com.nashtech.rootkies.enums.Gender;
import com.nashtech.rootkies.model.Location;
import com.nashtech.rootkies.model.Role;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateUserDTO {
    @NotBlank
    @Size(max = 30)
    private String firstName;

    @NotBlank
    @Size(max = 30)
    private String lastName;

    private String dateOfBirth;

    private String gender;

    @NotBlank
    private String joinedDate;

    @NotBlank
    private String role;

    @NotNull
    private Long location;
}
