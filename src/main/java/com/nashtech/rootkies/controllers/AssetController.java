package com.nashtech.rootkies.controllers;

import com.nashtech.rootkies.constants.ErrorCode;
import com.nashtech.rootkies.constants.SuccessCode;
import com.nashtech.rootkies.dto.common.ResponseDTO;
import com.nashtech.rootkies.exception.*;
import com.nashtech.rootkies.model.Asset;
import com.nashtech.rootkies.service.AssetService;
import com.nashtech.rootkies.specification.AssetSpecificationBuilder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nashtech.rootkies.constants.ErrorCode;
import com.nashtech.rootkies.constants.SuccessCode;
import com.nashtech.rootkies.converter.LocationConverter;
import com.nashtech.rootkies.dto.asset.AssetDTO;
import com.nashtech.rootkies.dto.asset.CreateAssetRequest;
import com.nashtech.rootkies.dto.asset.EditAssetRequest;
import com.nashtech.rootkies.dto.common.ResponseDTO;
import com.nashtech.rootkies.exception.AssetAssignedException;
import com.nashtech.rootkies.exception.ConvertEntityDTOException;
import com.nashtech.rootkies.exception.CreateDataFailException;
import com.nashtech.rootkies.exception.DataBlockedException;
import com.nashtech.rootkies.exception.DataNotFoundException;
import com.nashtech.rootkies.exception.InvalidRequestDataException;
import com.nashtech.rootkies.exception.UpdateDataFailException;
import com.nashtech.rootkies.model.Asset;
import com.nashtech.rootkies.security.jwt.JwtUtils;
import com.nashtech.rootkies.service.AssetService;
import com.nashtech.rootkies.specification.AssetSpecificationBuilder;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/assets")
public class AssetController {

    Logger logger = LoggerFactory.getLogger(AssetController.class);

    @Autowired
    AssetService assetService;

    @Autowired
    LocationConverter locationConverter;

    @Autowired
    JwtUtils jwtUtils;


    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO> createNewAsset (@Valid @RequestBody CreateAssetRequest createAssetRequest, HttpServletRequest request) throws DataNotFoundException, CreateDataFailException, InvalidRequestDataException {
        String jwt = request.getHeader("Authorization").substring(7);
        String username = jwtUtils.getUserNameFromJwtToken(jwt);
        try {
            Long locationID = locationConverter.getLocationFromUser(username);
            try {
                ResponseDTO responseDTO = assetService.createAsset(createAssetRequest, locationID);
                return ResponseEntity.ok().body(responseDTO);
            } catch (CreateDataFailException e) {
                logger.error(e.getMessage());
                throw new CreateDataFailException(e.getMessage());
            } catch (InvalidRequestDataException e) {
                logger.error(e.getMessage());
                throw new InvalidRequestDataException(e.getMessage());
            } catch (ConvertEntityDTOException e) {
                logger.error(e.getMessage());
                throw new ConvertEntityDTOException(e.getMessage());
            }
        } catch (DataNotFoundException | ConvertEntityDTOException e) {
            logger.error(e.getMessage());
            throw new DataNotFoundException(e.getMessage());
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO> getAllAsset(@RequestParam Integer page,
                                                   @RequestParam Integer size,
                                                   @RequestParam String sort,
                                                   @RequestParam String search) throws DataNotFoundException {
        ResponseDTO responseDTO = new ResponseDTO();
        try {
            Pageable pageable = null;
            if (sort.contains("ASC")) {
                pageable = PageRequest.of(page, size, Sort.by(sort.replace("ASC", "")).ascending());
            } else {
                pageable = PageRequest.of(page, size, Sort.by(sort.replace("DES", "")).descending());
            }

            AssetSpecificationBuilder builder = new AssetSpecificationBuilder();
            Pattern pattern = Pattern.compile("(\\w+?)(:|<|>|@)(\\w+?),");
            Matcher matcher = pattern.matcher(search + ",");
            while (matcher.find()) {
                builder.with(matcher.group(1), matcher.group(2), matcher.group(3));
            }

            Specification<Asset> spec = builder.build();

            responseDTO.setData(assetService.getAssetList(pageable, spec));
            responseDTO.setSuccessCode(SuccessCode.GET_ASSET_SUCCESS);
            logger.info(SuccessCode.GET_ASSET_SUCCESS);
            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            logger.error(ErrorCode.ERR_GET_ALL_ASSET);
            responseDTO.setErrorCode(ErrorCode.ERR_GET_ALL_ASSET);
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    @PutMapping("/delete/{assetcode}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO> deleteAsset(@PathVariable("assetcode") String assetCode) throws DataNotFoundException, UpdateDataFailException {
        ResponseDTO responseDTO = new ResponseDTO();
        try {
            boolean deletedAsset = assetService.deleteAsset(assetCode);
            responseDTO.setData(deletedAsset);
            responseDTO.setSuccessCode(SuccessCode.ASSET_DELETE_SUCCESS);
        } catch (UpdateDataFailException e) {
            logger.error(ErrorCode.ERR_ASSET_DELETE_FAIL);
            throw new UpdateDataFailException(e.getMessage());
        }
        logger.info(SuccessCode.ASSET_DELETE_SUCCESS);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/detail/{assetcode}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO> getAssetDetail(@PathVariable(value = "assetcode") @NotBlank String staffCode) throws DataNotFoundException, DataBlockedException, ConvertEntityDTOException {
        ResponseDTO responseDTO = new ResponseDTO();
        try {
            responseDTO.setData(assetService.getAssetDetailByAssetCode(staffCode));
            responseDTO.setSuccessCode(SuccessCode.GET_ASSET_SUCCESS);
        } catch (DataNotFoundException e) {
            logger.error(ErrorCode.ERR_ASSETCODE_NOT_FOUND);
            throw new DataNotFoundException(ErrorCode.ERR_ASSETCODE_NOT_FOUND);
        } catch (DataBlockedException e) {
            logger.error(ErrorCode.ASSET_IS_DELETED);
            throw new DataBlockedException(ErrorCode.ASSET_IS_DELETED);
        } catch (ConvertEntityDTOException e) {
            logger.error(ErrorCode.ERR_CONVERT_ENTITY_DTO_FAIL);
            throw new ConvertEntityDTOException(ErrorCode.ERR_CONVERT_ENTITY_DTO_FAIL);
        }
        return ResponseEntity.ok(responseDTO);
    }
    
    @GetMapping("/{assetCode}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO> getAsset(@PathVariable("assetCode") @NotBlank String assetCode) throws DataNotFoundException, AssetAssignedException, ConvertEntityDTOException {
    	ResponseDTO responseDTO = new ResponseDTO();
    	try {
    		AssetDTO assetDTO = assetService.getAsset(assetCode);
    		responseDTO.setData(assetDTO);
    		responseDTO.setSuccessCode(SuccessCode.GET_ASSET_SUCCESS);
    		return ResponseEntity.ok(responseDTO);
    	} catch (DataNotFoundException e) {
    		logger.error(e.getMessage());
    		throw new DataNotFoundException(e.getMessage());
		} catch (AssetAssignedException e) {
			logger.error(e.getMessage());
			throw new AssetAssignedException(e.getMessage());
		} catch (ConvertEntityDTOException e) {
			logger.error(e.getMessage());
			throw new ConvertEntityDTOException(e.getMessage());
		}
    }
    
    @PutMapping("/edit/{assetCode}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO> updateAsset(@PathVariable("assetCode") @NotBlank String assetCode, @RequestBody @Valid EditAssetRequest request) throws InvalidRequestDataException, DataNotFoundException, AssetAssignedException, ConvertEntityDTOException, UpdateDataFailException {
    	ResponseDTO responseDTO = new ResponseDTO();
    	try {
			responseDTO.setData(assetService.updateAsset(assetCode, request));
			responseDTO.setSuccessCode(SuccessCode.ASSET_EDIT_SUCCESS);
			return ResponseEntity.ok(responseDTO);
		} catch (InvalidRequestDataException e) {
			logger.error(e.getMessage());
			throw new InvalidRequestDataException(e.getMessage());
		} catch (DataNotFoundException e) {
			logger.error(e.getMessage());
			throw new DataNotFoundException(e.getMessage());
		} catch (AssetAssignedException e) {
			logger.error(e.getMessage());
			throw new AssetAssignedException(e.getMessage());
		} catch (ConvertEntityDTOException e) {
			logger.error(e.getMessage());
			throw new ConvertEntityDTOException(e.getMessage());
		} catch (UpdateDataFailException e) {
			logger.error(e.getMessage());
			throw new UpdateDataFailException(e.getMessage());
		}
    }
}
