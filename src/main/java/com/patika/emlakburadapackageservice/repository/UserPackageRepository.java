package com.patika.emlakburadapackageservice.repository;

import com.patika.emlakburadapackageservice.model.UserPackage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPackageRepository extends JpaRepository<UserPackage, Long> {
}
