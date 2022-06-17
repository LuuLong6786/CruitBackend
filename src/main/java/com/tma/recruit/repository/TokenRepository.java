package com.tma.recruit.repository;

import com.tma.recruit.model.entity.TokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.xml.crypto.Data;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<TokenEntity, Long> {

    Optional<TokenEntity> findByToken(String token);

    List<TokenEntity>  findByUserEntityId(Long id);

    boolean existsByToken(String token);

    boolean existsByTokenAndExpiredTimeGreaterThan(String token, Date date);
}
