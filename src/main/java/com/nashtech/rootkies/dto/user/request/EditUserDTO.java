package com.nashtech.rootkies.dto.user.request;

import com.nashtech.rootkies.enums.ERole;
import com.nashtech.rootkies.enums.Gender;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EditUserDTO {

    @NotBlank
    private String dateOfBirth;

    @NotBlank
    private String joinedDate;

    private String gender;

    @NotBlank
    private String role;

    @NotNull
    private Long location;
}
