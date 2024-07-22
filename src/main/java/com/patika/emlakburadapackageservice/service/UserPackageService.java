package com.patika.emlakburadapackageservice.service;

import com.patika.emlakburadapackageservice.client.service.PaymentService;
import com.patika.emlakburadapackageservice.model.UserPackage;
import com.patika.emlakburadapackageservice.repository.AdPackageRepository;
import com.patika.emlakburadapackageservice.repository.UserPackageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserPackageService {

    private final UserPackageRepository userPackageRepository;

    public void decrementPackageRights(Long userId) {
        List<UserPackage> userPackages = userPackageRepository.findAllByUserId(userId);

        userPackages.stream()
                .filter(pkg -> pkg.getExpiryDate().isAfter(LocalDateTime.now()) && pkg.getRemainingCount() > 0)
                .findFirst()
                .ifPresent(pkg -> {
                    pkg.setRemainingCount(pkg.getRemainingCount() - 1);
                    userPackageRepository.save(pkg);
                });
    }
    public void save(UserPackage userPackage) {
        userPackageRepository.save(userPackage);
    }

    public List<UserPackage> getPackagesById(Long id) {
        return userPackageRepository.findAllByUserId(id);
    }

}
