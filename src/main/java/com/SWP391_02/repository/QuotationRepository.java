package com.SWP391_02.repository;

import com.SWP391_02.entity.Quotation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuotationRepository extends JpaRepository<Quotation, Long> {
    List<Quotation> findByRepairId(Long repairId);
}
