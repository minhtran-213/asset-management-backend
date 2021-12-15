package com.nashtech.rootkies.repository;

import com.nashtech.rootkies.model.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    @Query(value = "SELECT CASE WHEN count(assignmentid) > 0 THEN true ELSE false END checkExist "+
            "FROM assignments a WHERE assignedby = ?1 or assignedto =?1 " , nativeQuery = true)
    Boolean checkUserHasValidAssignment(String staffCode);
}
