package com.tma.recruit.repository;

import com.tma.recruit.model.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {

    Optional<Token> findByToken(String token);

    List<Token> findByUserId(Long id);

    boolean existsByToken(String token);

    boolean existsByTokenAndExpiredTimeGreaterThan(String token, Date date);
}
