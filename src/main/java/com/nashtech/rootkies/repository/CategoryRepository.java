package com.nashtech.rootkies.repository;

import com.nashtech.rootkies.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {
    Boolean existsByCategoryName(String name);

    Optional<Category> findByCategoryName(String name);

    Page<Category> findAll(Specification<Category> spec, Pageable pageable);
}
