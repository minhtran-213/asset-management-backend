package com.nashtech.rootkies.controllers;

import com.nashtech.rootkies.dto.common.ResponseDTO;
import com.nashtech.rootkies.exception.DataNotFoundException;
import com.nashtech.rootkies.dto.category.CreateCategoryRequest;
import com.nashtech.rootkies.exception.ConvertEntityDTOException;
import com.nashtech.rootkies.exception.CreateDataFailException;
import com.nashtech.rootkies.exception.DataNotFoundException;
import com.nashtech.rootkies.exception.DuplicateDataException;
import com.nashtech.rootkies.service.CategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/categories")
public class CategoryController {
    Logger logger = LoggerFactory.getLogger(CategoryController.class);

    @Autowired
    CategoryService categoryService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO> createCategory (@Valid @RequestBody CreateCategoryRequest request) throws CreateDataFailException, DuplicateDataException, ConvertEntityDTOException {
        try {
            ResponseDTO responseDTO = categoryService.createNewCategory(request);
            return ResponseEntity.ok().body(responseDTO);
        } catch (CreateDataFailException e) {
            logger.error(e.getMessage());
            throw new CreateDataFailException(e.getMessage());
        } catch (DuplicateDataException e) {
            logger.error(e.getMessage());
            throw new DuplicateDataException(e.getMessage());
        } catch (ConvertEntityDTOException e) {
            logger.error(e.getMessage());
            throw new ConvertEntityDTOException(e.getMessage());
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO> getAllCategories () throws DataNotFoundException, ConvertEntityDTOException {
        try {
            ResponseDTO responseDTO = categoryService.getCategoryList();
            return ResponseEntity.ok().body(responseDTO);
        } catch (DataNotFoundException e) {
            logger.error(e.getMessage());
            throw new DataNotFoundException(e.getMessage());
        } catch (ConvertEntityDTOException e) {
            logger.error(e.getMessage());
            throw new ConvertEntityDTOException(e.getMessage());
        }
    }
}
