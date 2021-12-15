package com.nashtech.rootkies.dto.user.request;

import com.nashtech.rootkies.enums.ERole;
import com.nashtech.rootkies.enums.SortType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Optional;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class GetUserListDTO {
	private Optional<String> searchString;
	private Optional<String> role;
	private Optional<String> sortColumn;
	private Optional<String> sortType;
	private Optional<Integer> pageNumber;
	private Optional<Integer> pageSize;
}
