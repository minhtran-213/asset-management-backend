package com.nashtech.rootkies.dto.asset;

import com.nashtech.rootkies.constants.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateAssetRequest {

    @NotBlank
    private String assetName;

    @NotNull
    private Short state;

    @NotNull
    private String installDate;

    @NotBlank
    private String specification;

    @NotBlank
    @Pattern(regexp = "(^[A-Z]{2,3}$)", message = ErrorCode.ERR_CATEGORY_IDS_NOT_CORRECT)
    private String categoryCode;
}
