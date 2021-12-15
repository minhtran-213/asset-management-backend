package com.nashtech.rootkies.converter;

import com.nashtech.rootkies.constants.ErrorCode;
import com.nashtech.rootkies.dto.category.CategoryDTO;

import com.nashtech.rootkies.dto.category.CreateCategoryRequest;

import com.nashtech.rootkies.exception.ConvertEntityDTOException;
import com.nashtech.rootkies.model.Category;
import com.nashtech.rootkies.repository.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import java.util.stream.Collectors;
import com.nashtech.rootkies.dto.category.CreateCategoryRequest;
import com.nashtech.rootkies.exception.DuplicateDataException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class CategoryConverter {

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    CategoryRepository categoryRepository;

    Logger logger = LoggerFactory.getLogger(CategoryConverter.class);

    public Category convertCreateRequestToEntity (CreateCategoryRequest createCategoryRequest) throws DuplicateDataException, ConvertEntityDTOException {
        Optional<Category> categoryByName = categoryRepository.findByCategoryName(createCategoryRequest.getCategoryName());
        if (categoryByName.isPresent()) {
            throw new DuplicateDataException(ErrorCode.ERR_CATEGORY_NAME_EXISTED);
        }

        Optional<Category> categoryById = categoryRepository.findById(createCategoryRequest.getCategoryCode());
        if(categoryById.isPresent()){
            throw new DuplicateDataException(ErrorCode.ERR_CATEGORY_CODE_EXISTED);
        }

        try {
            return modelMapper.map(createCategoryRequest, Category.class);
        } catch (Exception e){
            logger.error(e.getMessage());
            throw new ConvertEntityDTOException(ErrorCode.ERR_CONVERT_DTO_ENTITY_FAIL);
        }
    }

    public CategoryDTO convertToDTO (Category category) throws ConvertEntityDTOException {
        try {
            return modelMapper.map(category, CategoryDTO.class);
        } catch (Exception e){
            logger.error(e.getMessage());
            throw new ConvertEntityDTOException(ErrorCode.ERR_CONVERT_ENTITY_DTO_FAIL);
        }
    }


    public CategoryDTO convertEntityToCategoryDTO(Category category) throws ConvertEntityDTOException {
        try {
            CategoryDTO dto = modelMapper.map(category, CategoryDTO.class);
            return dto;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ConvertEntityDTOException(ErrorCode.ERR_CONVERT_DTO_ENTITY_FAIL);
        }
    }

    public List<CategoryDTO> convertToListCategoryDTO(List<Category> categories) throws ConvertEntityDTOException {
        try {
            return categories.stream().map(category -> {
                try {
                    return convertEntityToCategoryDTO(category);
                } catch (ConvertEntityDTOException e) {
                    logger.error(e.getMessage());
                }
                return new CategoryDTO();
            }).collect(Collectors.toList());
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new ConvertEntityDTOException(ErrorCode.ERR_CONVERT_DTO_ENTITY_FAIL);
        }
    }
}
