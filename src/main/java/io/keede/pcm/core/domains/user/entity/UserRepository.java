package io.keede.pcm.core.domains.user.entity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(
            final String email
    );

    boolean existsByIdAndTokenKey(
            final Long id,
            final String tokenKey
    );

    Optional<User> findByEmail(
            final String email
    );
}
