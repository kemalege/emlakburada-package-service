package com.patika.emlakburadapackageservice.repository;

import com.patika.emlakburadapackageservice.model.UserPackage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserPackageRepository extends JpaRepository<UserPackage, Long> {
    List<UserPackage> findAllByUserId(Long userId);
}
