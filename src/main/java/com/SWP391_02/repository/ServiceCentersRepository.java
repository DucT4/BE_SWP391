package com.SWP391_02.repository;

import com.SWP391_02.entity.ServiceCenters;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceCentersRepository extends JpaRepository<ServiceCenters, Long> {

}
