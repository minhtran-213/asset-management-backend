package com.nashtech.rootkies.dto.user;

import lombok.*;

import java.time.LocalDate;


@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class ManageUserResponse {
    private String staffCode;
    private String fullName;
    private String username;
    private LocalDate joinedDate;
    private String type;
}
