package com.nashtech.rootkies.repository;

import com.nashtech.rootkies.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    Location findByLocationId(Long id);
    Location findByAddress(String address);
}
