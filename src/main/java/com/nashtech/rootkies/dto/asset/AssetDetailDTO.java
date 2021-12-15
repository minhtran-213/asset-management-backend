package com.nashtech.rootkies.dto.asset;

import lombok.*;

import java.time.LocalDate;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AssetDetailDTO {

    private String assetCode;
    private String assetName;
    private String category;
    private LocalDate installedDate;
    private String state;
    private String location;
    private String specification;
}
