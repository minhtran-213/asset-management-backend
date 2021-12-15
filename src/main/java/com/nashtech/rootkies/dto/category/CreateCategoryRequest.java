package com.nashtech.rootkies.dto.category;

import com.nashtech.rootkies.constants.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateCategoryRequest {
    @NotBlank
    @Pattern(regexp = "(^[A-Z]{2,3}$)", message = ErrorCode.ERR_CATEGORY_IDS_NOT_CORRECT)
    private String categoryCode;

    @NotBlank
    private String categoryName;
}
