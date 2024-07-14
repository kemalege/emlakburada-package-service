package com.patika.emlakburadapackageservice.converter;

import com.patika.emlakburadapackageservice.consumer.dto.NotificationDto;
import com.patika.emlakburadapackageservice.model.AdPackage;
import com.patika.emlakburadapackageservice.model.UserPackage;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserPackageConverter {

    public static UserPackage convert(NotificationDto request, AdPackage adPackage) {
        return UserPackage.builder()
                .userId(request.getUserId())
                .packageId(request.getPackageId())
                .expiryDate(LocalDateTime.now().plusDays(adPackage.getValidityDays()))
                .remainingCount(adPackage.getAdCount())
                .build();
    }
}
