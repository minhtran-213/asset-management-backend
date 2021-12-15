package com.nashtech.rootkies.service;

import com.nashtech.rootkies.dto.category.CreateCategoryRequest;
import com.nashtech.rootkies.dto.common.ResponseDTO;
import com.nashtech.rootkies.exception.ConvertEntityDTOException;
import com.nashtech.rootkies.exception.CreateDataFailException;
import com.nashtech.rootkies.exception.DataNotFoundException;
import com.nashtech.rootkies.exception.DuplicateDataException;
import com.nashtech.rootkies.model.Category;

public interface CategoryService {
    ResponseDTO createNewCategory(CreateCategoryRequest categoryRequest) throws CreateDataFailException, DuplicateDataException, ConvertEntityDTOException;
    ResponseDTO getCategoryList() throws DataNotFoundException, ConvertEntityDTOException;

}
