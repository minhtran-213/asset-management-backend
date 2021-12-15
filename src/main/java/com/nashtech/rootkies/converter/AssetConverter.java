package com.nashtech.rootkies.converter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.nashtech.rootkies.constants.ErrorCode;
import com.nashtech.rootkies.constants.State;
import com.nashtech.rootkies.dto.PageDTO;
import com.nashtech.rootkies.dto.asset.AssetDTO;
import com.nashtech.rootkies.dto.asset.AssetDetailDTO;
import com.nashtech.rootkies.dto.asset.CreateAssetRequest;
import com.nashtech.rootkies.dto.asset.EditAssetRequest;
import com.nashtech.rootkies.dto.asset.ManageAssetResponse;
import com.nashtech.rootkies.exception.ConvertEntityDTOException;
import com.nashtech.rootkies.exception.InvalidRequestDataException;
import com.nashtech.rootkies.model.Asset;
import com.nashtech.rootkies.model.Category;
import com.nashtech.rootkies.model.Location;
import com.nashtech.rootkies.repository.CategoryRepository;
import com.nashtech.rootkies.repository.LocationRepository;

@Component
public class AssetConverter {
    @Autowired
    ModelMapper modelMapper;

    @Autowired
    LocationRepository locationRepository;

    @Autowired
    CategoryRepository categoryRepository;

    Logger logger = LoggerFactory.getLogger(AssetConverter.class);

    public Asset convertCreateRequestToEntity(CreateAssetRequest createAssetRequest, Long locationID) throws InvalidRequestDataException, ConvertEntityDTOException {
        Category category = categoryRepository.findById(createAssetRequest.getCategoryCode()).orElseThrow(() -> new InvalidRequestDataException(ErrorCode.ERR_CATEGORY_NOT_FOUND));
        Location location = locationRepository.findById(locationID).orElseThrow(() -> new InvalidRequestDataException(ErrorCode.ERR_LOCATION_NOT_FAIL));
        Asset asset = null;
        try {
            asset = modelMapper.map(createAssetRequest, Asset.class);
        } catch (Exception e){
            logger.error(e.getMessage());
            throw new ConvertEntityDTOException(ErrorCode.ERR_CONVERT_DTO_ENTITY_FAIL);
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        asset.setInstallDate(LocalDateTime.parse(createAssetRequest.getInstallDate(), formatter));
        asset.setCategory(category);
        asset.setLocation(location);
        asset.setIsDeleted(false);

        return asset;
    }

    public ManageAssetResponse entityToManageAssetResponse (Asset asset) {
        String state;
        if(asset.getState().equals(State.AVAILABLE)) {
            state = "AVAILABLE";
        } else if (asset.getState().equals(State.NOT_AVAILABLE)) {
            state = "NOT_AVAILABLE";
        } else if (asset.getState().equals(State.ASSIGNED)) {
            state = "ASSIGNED";
        } else if (asset.getState().equals(State.WAITING_FOR_RECYCLING)) {
            state = "WAITING_FOR_RECYCLING";
        } else {
            state = "RECYCLED";
        }
        return ManageAssetResponse.builder()
                .assetCode(asset.getAssetCode())
                .assetName(asset.getAssetName())
                .category(asset.getCategory().getCategoryName())
                .state(state)
                .build();
    }

    public PageDTO pageToPageDTO (Page<Asset> page){
        List<ManageAssetResponse> assets = page.stream()
                .map(this::entityToManageAssetResponse)
                .collect(Collectors.toList());

        return PageDTO.builder().totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .data(assets)
                .build();
    }

    public AssetDetailDTO entityToDetailDTO(Asset asset) throws ConvertEntityDTOException {
        String state;
        if(asset.getState().equals(State.AVAILABLE)) {
            state = "AVAILABLE";
        } else if (asset.getState().equals(State.NOT_AVAILABLE)) {
            state = "NOT_AVAILABLE";
        } else if (asset.getState().equals(State.ASSIGNED)) {
            state = "ASSIGNED";
        } else if (asset.getState().equals(State.WAITING_FOR_RECYCLING)) {
            state = "WAITING_FOR_RECYCLING";
        } else {
            state = "RECYCLED";
        }
        try {
            return AssetDetailDTO.builder()
                    .assetCode(asset.getAssetCode())
                    .assetName(asset.getAssetName())
                    .category(asset.getCategory().getCategoryName())
                    .installedDate(asset.getInstallDate().toLocalDate())
                    .state(state)
                    .location(asset.getLocation().getAddress())
                    .specification(asset.getSpecification())
                    .build();
        } catch (Exception e) {
            throw new ConvertEntityDTOException(ErrorCode.ERR_CONVERT_ENTITY_DTO_FAIL);
        }
    }
    
    public AssetDTO entityToAssetDTO(Asset asset) throws ConvertEntityDTOException {
    	try {
    		AssetDTO assetDTO = modelMapper.map(asset, AssetDTO.class);
    		assetDTO.setCategoryCode(asset.getCategory().getCategoryCode());
        	assetDTO.setCategoryName(asset.getCategory().getCategoryName());
        	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        	assetDTO.setInstalledDate(asset.getInstallDate().format(formatter));
        	return assetDTO;
    	} catch (Exception e) {
			throw new ConvertEntityDTOException(ErrorCode.ERR_CONVERT_ENTITY_DTO_FAIL);
		}
    }
    
    public Asset editAssetRequestToEntity(EditAssetRequest request) throws ConvertEntityDTOException {
    	try {
    		Asset asset = modelMapper.map(request, Asset.class);
    		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    		asset.setInstallDate(LocalDateTime.parse(request.getInstalledDate(), formatter));
    		return asset;
		} catch (Exception e) {
			throw new ConvertEntityDTOException(ErrorCode.ERR_CONVERT_DTO_ENTITY_FAIL);
		}
    }
}
