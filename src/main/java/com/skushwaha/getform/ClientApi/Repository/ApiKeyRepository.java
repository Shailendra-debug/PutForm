package com.skushwaha.getform.ClientApi.Repository;

import com.skushwaha.getform.ClientApi.Entity.ApiKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ApiKeyRepository
        extends JpaRepository<ApiKey, Long> {


    @Query("""
    SELECT a
    FROM ApiKey a
    JOIN FETCH a.user
    WHERE a.keyHash = :keyHash
      AND a.active = true
""")
    Optional<ApiKey> findActiveByKeyHashWithUser(
            @Param("keyHash") String keyHash
    );

    @Query("""
        SELECT a
        FROM ApiKey a
        JOIN FETCH a.user
        WHERE a.keyHash = :keyHash
          AND a.active = true
    """)
    Optional<ApiKey> findByKeyHashAndActiveTrue(String keyHash);

    List<ApiKey> findByUserId(Long userId);

    Optional<ApiKey> findByIdAndUserId(Long id, Long userId);

}
