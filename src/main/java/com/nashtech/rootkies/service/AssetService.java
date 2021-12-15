package com.nashtech.rootkies.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.nashtech.rootkies.dto.PageDTO;
import com.nashtech.rootkies.dto.asset.AssetDTO;
import com.nashtech.rootkies.dto.asset.AssetDetailDTO;
import com.nashtech.rootkies.dto.asset.CreateAssetRequest;
import com.nashtech.rootkies.dto.asset.EditAssetRequest;
import com.nashtech.rootkies.dto.common.ResponseDTO;
import com.nashtech.rootkies.exception.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import com.nashtech.rootkies.exception.AssetAssignedException;
import com.nashtech.rootkies.exception.ConvertEntityDTOException;
import com.nashtech.rootkies.exception.CreateDataFailException;
import com.nashtech.rootkies.exception.DataBlockedException;
import com.nashtech.rootkies.exception.DataNotFoundException;
import com.nashtech.rootkies.exception.InvalidRequestDataException;
import com.nashtech.rootkies.exception.UpdateDataFailException;

public interface AssetService {
    ResponseDTO createAsset(CreateAssetRequest createAssetRequest, Long locationID) throws CreateDataFailException, InvalidRequestDataException, ConvertEntityDTOException;

    PageDTO getAssetList(Pageable pageable, Specification specification) throws DataNotFoundException;

    boolean deleteAsset(String assetCode) throws DataNotFoundException, UpdateDataFailException;

    AssetDetailDTO getAssetDetailByAssetCode(String assetCode) throws DataNotFoundException, DataBlockedException, ConvertEntityDTOException;
	
    AssetDTO getAsset(String assetCode) throws DataNotFoundException, AssetAssignedException, ConvertEntityDTOException;

	Boolean updateAsset(String assetCode, EditAssetRequest request) throws InvalidRequestDataException, DataNotFoundException, AssetAssignedException, ConvertEntityDTOException, UpdateDataFailException;
}
