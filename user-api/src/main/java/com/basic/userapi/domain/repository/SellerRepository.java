package com.basic.userapi.domain.repository;

import com.basic.userapi.domain.model.Seller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SellerRepository extends JpaRepository<Seller, Long> {
    Optional<Seller> findByEmailAndPasswordAndVerifyIsTrue(String email, String password);
    Optional<Seller> findByEmail(String email);
}
