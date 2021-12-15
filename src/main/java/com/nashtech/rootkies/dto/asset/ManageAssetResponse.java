package com.nashtech.rootkies.dto.asset;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class ManageAssetResponse {
    private String assetCode;
    private String assetName;
    private String category;
    private String state;
}
