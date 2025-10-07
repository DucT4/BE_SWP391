package com.SWP391_02.repository;


import com.SWP391_02.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import com.SWP391_02.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByRole(Role role);   // dùng để kiểm tra đã có ADMIN chưa
    long countByRole(Role role);       // (nếu muốn đếm)
}
