package com.SWP391_02.repository;

import com.SWP391_02.entity.WarrantyCoverage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface WarrantyCoverageRepository extends JpaRepository<WarrantyCoverage, Long> {
    List<WarrantyCoverage> findByVin(String vin);

    Page<WarrantyCoverage> findByVinOrderByEndDateDesc(String vin, Pageable pageable);

    // Coverages còn hiệu lực theo ngày
    List<WarrantyCoverage> findByVinAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            String vin, LocalDate date1, LocalDate date2);
}