package com.sherly.ifashion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.sherly.ifashion.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
