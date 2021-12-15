package com.nashtech.rootkies.dto.asset;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AssetDTO {
	private String assetName;
	private String categoryCode;
	private String categoryName;
	private String specification;
	private String installedDate;
	private Short state;
}
