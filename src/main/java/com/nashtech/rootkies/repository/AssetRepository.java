package com.nashtech.rootkies.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nashtech.rootkies.model.Asset;

@Repository
public interface AssetRepository extends JpaRepository<Asset, String> {
    Page<Asset> findAll(Specification<Asset> spec, Pageable pageable);

    Optional<Asset> findByAssetCode(String assetCode);
}
