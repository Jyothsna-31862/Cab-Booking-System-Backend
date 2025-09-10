package com.cabbooking.repository;

import com.cabbooking.entity.Driver;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface DriverRepository extends JpaRepository<Driver, String> {
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);

    Optional<Driver> findByDriverId(String id);
 }
