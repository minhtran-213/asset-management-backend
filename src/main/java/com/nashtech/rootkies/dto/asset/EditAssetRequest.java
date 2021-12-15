package com.nashtech.rootkies.dto.asset;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EditAssetRequest {
	
	@NotBlank
	private String assetName;
	
	@NotBlank
	private String specification;
	
	@NotBlank
	private String installedDate;
	
	@NotNull
	private Short state;
}
