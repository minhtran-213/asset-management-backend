package com.nashtech.rootkies.service.impl;

import com.nashtech.rootkies.constants.ErrorCode;
import com.nashtech.rootkies.converter.AssetConverter;
import com.nashtech.rootkies.dto.PageDTO;
import com.nashtech.rootkies.exception.*;
import com.nashtech.rootkies.model.Asset;
import com.nashtech.rootkies.repository.AssetRepository;
import com.nashtech.rootkies.service.AssetService;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.nashtech.rootkies.constants.ErrorCode;
import com.nashtech.rootkies.constants.State;
import com.nashtech.rootkies.constants.SuccessCode;
import com.nashtech.rootkies.converter.AssetConverter;
import com.nashtech.rootkies.dto.PageDTO;
import com.nashtech.rootkies.dto.asset.AssetDTO;
import com.nashtech.rootkies.dto.asset.AssetDetailDTO;
import com.nashtech.rootkies.dto.asset.CreateAssetRequest;
import com.nashtech.rootkies.dto.asset.EditAssetRequest;
import com.nashtech.rootkies.dto.common.ResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.nashtech.rootkies.exception.AssetAssignedException;
import com.nashtech.rootkies.exception.ConvertEntityDTOException;
import com.nashtech.rootkies.exception.CreateDataFailException;
import com.nashtech.rootkies.exception.DataBlockedException;
import com.nashtech.rootkies.exception.DataNotFoundException;
import com.nashtech.rootkies.exception.InvalidRequestDataException;
import com.nashtech.rootkies.exception.UpdateDataFailException;
import com.nashtech.rootkies.model.Asset;
import com.nashtech.rootkies.repository.AssetRepository;
import com.nashtech.rootkies.service.AssetService;

import java.util.Optional;

@Service
public class AssetServiceImpl implements AssetService {

    @Autowired
    AssetRepository assetRepository;

    @Autowired
    AssetConverter assetConverter;

    Logger LOGGER = LoggerFactory.getLogger(AssetServiceImpl.class);

    @Override
    public ResponseDTO createAsset(CreateAssetRequest createAssetRequest, Long locationID) throws CreateDataFailException, InvalidRequestDataException, ConvertEntityDTOException {

        if (!createAssetRequest.getState().equals(State.AVAILABLE) && !createAssetRequest.getState().equals(State.NOT_AVAILABLE)) {
            throw new InvalidRequestDataException(ErrorCode.ERR_ASSET_STATE_NOT_CORRECT);
        }

        ResponseDTO responseDTO = new ResponseDTO();
        Asset asset = null;
        try {
            asset = assetConverter.convertCreateRequestToEntity(createAssetRequest, locationID);
            try {
                assetRepository.save(asset);
                responseDTO.setData(true);
                responseDTO.setSuccessCode(SuccessCode.ASSET_CREATED_SUCCESS);
                return responseDTO;
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
                throw new CreateDataFailException(ErrorCode.ERR_CREATE_ASSET_FAIL);
            }
        } catch (ConvertEntityDTOException e) {
            LOGGER.error(e.getMessage());
            throw new ConvertEntityDTOException(e.getMessage());
        }
    }

    @Override
    public PageDTO getAssetList(Pageable pageable, Specification specification) throws DataNotFoundException {
        try {
            Page<Asset> page = assetRepository.findAll(specification, pageable);
            PageDTO pageDTO = assetConverter.pageToPageDTO(page);
            return pageDTO;
        } catch (Exception e) {
            throw new DataNotFoundException(ErrorCode.ERR_GET_ALL_ASSET);
        }
    }

    @Override
    public boolean deleteAsset(String assetCode) throws DataNotFoundException, UpdateDataFailException {
        Optional<Asset> optionalAsset = assetRepository.findByAssetCode(assetCode);
        if(optionalAsset.isEmpty()) {
            throw new DataNotFoundException(ErrorCode.ERR_ASSETCODE_NOT_FOUND);
        }

        Asset asset = optionalAsset.get();
        try {
            asset.setIsDeleted(true);
            assetRepository.save(asset);
            return true;
        } catch (Exception e) {
            throw new UpdateDataFailException(e.getMessage());
        }
    }

    @Override
    public AssetDetailDTO getAssetDetailByAssetCode(String assetCode) throws DataNotFoundException, DataBlockedException, ConvertEntityDTOException {
        Asset asset = assetRepository.findByAssetCode(assetCode).orElseThrow(() ->
                new DataNotFoundException(ErrorCode.ERR_ASSETCODE_NOT_FOUND));

        if(asset.getIsDeleted()) {
            throw new DataBlockedException(ErrorCode.ASSET_IS_DELETED);
        }
        return assetConverter.entityToDetailDTO(asset);
    }
    
	@Override
	public AssetDTO getAsset(String assetCode) throws DataNotFoundException, AssetAssignedException, ConvertEntityDTOException {
		Asset asset = assetRepository.findByAssetCode(assetCode).orElseThrow(() ->
				new DataNotFoundException(ErrorCode.ERR_RETRIEVE_ASSET_FAIL));
		if (asset.getState() == State.ASSIGNED) {
			throw new AssetAssignedException(ErrorCode.ERR_ASSET_ALREADY_HAVE_ASSIGNMENT);
		}
		return assetConverter.entityToAssetDTO(asset);
	}

	@Override
	public Boolean updateAsset(String assetCode, EditAssetRequest request) throws InvalidRequestDataException, DataNotFoundException, AssetAssignedException, ConvertEntityDTOException, UpdateDataFailException {
		Short[] stateArr = {State.AVAILABLE, State.NOT_AVAILABLE, State.WAITING_FOR_ACCEPTANCE, State.RECYLED};
		if (!Arrays.stream(stateArr).anyMatch(item -> item == request.getState())) throw new InvalidRequestDataException(ErrorCode.ERR_ASSET_STATE_NOT_CORRECT);
		
		Asset assetExist = assetRepository.findByAssetCode(assetCode).orElseThrow(() ->
				new DataNotFoundException(ErrorCode.ERR_RETRIEVE_ASSET_FAIL));
		if (assetExist.getState() == State.ASSIGNED) {
			throw new AssetAssignedException(ErrorCode.ERR_ASSET_ALREADY_HAVE_ASSIGNMENT);
		}
		
		try {
			Asset editAssetRequest = assetConverter.editAssetRequestToEntity(request);
			
			assetExist.setAssetName(editAssetRequest.getAssetName());
			assetExist.setSpecification(editAssetRequest.getSpecification());
			assetExist.setInstallDate(editAssetRequest.getInstallDate());
			assetExist.setState(editAssetRequest.getState());
			
			assetRepository.save(assetExist);
			return true;
		} catch (ConvertEntityDTOException e) {
			throw new ConvertEntityDTOException(e.getMessage());
		} catch (Exception e) {
			throw new UpdateDataFailException(ErrorCode.ERR_EDIT_ASSET);
		}
	}
}
