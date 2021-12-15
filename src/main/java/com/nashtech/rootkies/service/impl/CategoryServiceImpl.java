package com.nashtech.rootkies.service.impl;

import com.nashtech.rootkies.constants.ErrorCode;
import com.nashtech.rootkies.constants.SuccessCode;
import com.nashtech.rootkies.controllers.CategoryController;
import com.nashtech.rootkies.converter.CategoryConverter;
import com.nashtech.rootkies.dto.category.CategoryDTO;
import com.nashtech.rootkies.dto.category.CreateCategoryRequest;
import com.nashtech.rootkies.dto.common.ResponseDTO;
import com.nashtech.rootkies.exception.DataNotFoundException;
import com.nashtech.rootkies.exception.ConvertEntityDTOException;
import com.nashtech.rootkies.exception.CreateDataFailException;
import com.nashtech.rootkies.exception.DuplicateDataException;
import com.nashtech.rootkies.model.Category;
import com.nashtech.rootkies.repository.CategoryRepository;
import com.nashtech.rootkies.service.CategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CategoryController.class);

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryConverter converter;


    @Override
    public ResponseDTO getCategoryList() throws DataNotFoundException, ConvertEntityDTOException {try {
            ResponseDTO responseDTO = new ResponseDTO();
            List<Category> categories;
            try {
                categories = categoryRepository.findAll(Sort.by("categoryName").ascending());
            } catch (Exception e) {
                LOGGER.error(ErrorCode.ERR_RETRIEVE_CATEGORY_FAIL);
                throw new DataNotFoundException(ErrorCode.ERR_RETRIEVE_CATEGORY_FAIL);
            }
            List<CategoryDTO> dtoList = converter.convertToListCategoryDTO(categories);
            responseDTO.setData(dtoList);
            responseDTO.setSuccessCode(SuccessCode.CATEGORIES_LOADED_SUCCESS);
            LOGGER.info(SuccessCode.CATEGORIES_LOADED_SUCCESS);
            return responseDTO;
        } catch (ConvertEntityDTOException e) {
            LOGGER.error(e.getMessage());
            throw new ConvertEntityDTOException(e.getMessage());
        }
    }

    @Override
    public ResponseDTO createNewCategory(CreateCategoryRequest categoryRequest) throws CreateDataFailException, DuplicateDataException, ConvertEntityDTOException {

        Category category = null;
        try {
            category = converter.convertCreateRequestToEntity(categoryRequest);
            try {
                categoryRepository.save(category);
                return new ResponseDTO(null, true, SuccessCode.CATEGORY_CREATED_SUCCESS);
            } catch (Exception e){
                LOGGER.error(e.getMessage());
                throw new CreateDataFailException(ErrorCode.ERR_CREATE_CATEGORY_FAIL);
            }
        } catch (DuplicateDataException e) {
            LOGGER.error(e.getMessage());
            throw new DuplicateDataException(e.getMessage());
        } catch (ConvertEntityDTOException e) {
            LOGGER.error(e.getMessage());
            throw new ConvertEntityDTOException(e.getMessage());
        }
    }

}
