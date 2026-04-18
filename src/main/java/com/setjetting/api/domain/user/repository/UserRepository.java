package com.setjetting.api.domain.user.repository;

import com.setjetting.api.domain.user.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String username);
    Optional<User> findByProviderIdAndProvider(String providerId, String provider);

    boolean existsByEmail(String email);


}
