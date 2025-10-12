package com.SWP391_02.repository;


import com.SWP391_02.entity.Part;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PartRepository extends JpaRepository<Part, Long> {
    Page<Part> findByPartNoContainingIgnoreCaseOrNameContainingIgnoreCase(String q1, String q2, Pageable pageable);
    boolean existsByPartNo(String partNo);
    Optional<Part> findByPartNo(String partNo);

}
