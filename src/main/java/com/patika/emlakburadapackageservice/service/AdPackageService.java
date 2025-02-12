package com.patika.emlakburadapackageservice.service;

import com.patika.emlakburadapackageservice.client.dto.response.PaymentResponse;
import com.patika.emlakburadapackageservice.client.service.PaymentService;
import com.patika.emlakburadapackageservice.consumer.dto.NotificationDto;
import com.patika.emlakburadapackageservice.converter.PackagePaymentRequestConverter;
import com.patika.emlakburadapackageservice.converter.UserPackageConverter;
import com.patika.emlakburadapackageservice.dto.request.PackagePaymentRequest;
import com.patika.emlakburadapackageservice.dto.request.PurchasePackageRequest;
import com.patika.emlakburadapackageservice.exception.EmlakBuradaException;
import com.patika.emlakburadapackageservice.exception.ExceptionMessages;
import com.patika.emlakburadapackageservice.model.AdPackage;
import com.patika.emlakburadapackageservice.model.UserPackage;
import com.patika.emlakburadapackageservice.repository.AdPackageRepository;
import com.patika.emlakburadapackageservice.repository.UserPackageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class AdPackageService {

    private final PaymentService paymentService;
    private final UserPackageService userPackageService;
    private final AdPackageRepository adPackageRepository;

    public void redirectToPaymentService(PurchasePackageRequest request) {

        AdPackage adPackage = getById(request.getPackageId());

        log.info("found adPackage: {}", adPackage.toString());

        PackagePaymentRequest packagePaymentRequest = PackagePaymentRequestConverter.convert(request, adPackage.getPrice());

        PaymentResponse paymentResponse = paymentService.purchasePackage(packagePaymentRequest);

        log.info("payment status. {}", paymentResponse.getStatus());

    }

    @RabbitListener(queues = "${notification.queue}")
    public void assignPackageToUser(NotificationDto notificationDto) {
        log.info("Received payment details: {}", notificationDto);

        AdPackage adPackageToBuy = getById(notificationDto.getPackageId());

        List<UserPackage> userPackages = userPackageService.getPackagesById(notificationDto.getUserId());

        if (userPackages.isEmpty()) {
            UserPackage newUserPackage = UserPackageConverter.convert(notificationDto, adPackageToBuy);
            userPackageService.save(newUserPackage);
            log.info("New package assigned to user: {}", notificationDto.getUserId());
        } else {
            extendExistingPackage(userPackages, adPackageToBuy);
            log.info("Existing package extended for user: {}", notificationDto.getUserId());
        }

    }

    public void extendExistingPackage(List<UserPackage> userPackages, AdPackage adPackageToBuy) {
        UserPackage lastPackage = userPackages.stream()
                .max(Comparator.comparing(UserPackage::getExpiryDate))
                .orElseThrow(() -> new IllegalStateException("No packages found for user"));

        lastPackage.setExpiryDate(lastPackage.getExpiryDate().plusDays(adPackageToBuy.getValidityDays()));
        lastPackage.setRemainingCount(lastPackage.getRemainingCount() + adPackageToBuy.getAdCount());
        userPackageService.save(lastPackage);
    }

    public Boolean checkPublishingRights(Long userId) {
        List<UserPackage> userPackages = userPackageService.getPackagesById(userId);

        boolean expiryDateCheck = userPackages.stream()
                .anyMatch(pkg -> pkg.getExpiryDate().isAfter(LocalDateTime.now()));

        boolean adCountCheck = userPackages.stream()
                .anyMatch(pkg -> pkg.getRemainingCount() > 0);

        if (!expiryDateCheck && adCountCheck) {
//            return new AdPackageAvailabilityResponse(false, ExceptionMessages.PACKET_EXPIRED);
            throw new EmlakBuradaException(ExceptionMessages.PACKET_EXPIRED);
        } else if (expiryDateCheck && !adCountCheck) {
//            return new AdPackageAvailabilityResponse(false, ExceptionMessages.INSUFFICIENT_AD_COUNT);
            throw new EmlakBuradaException(ExceptionMessages.INSUFFICIENT_AD_COUNT);
        } else {
            return expiryDateCheck;
        }
    }

    @Cacheable(value = "adpackage", cacheNames = "adpackage")
    public List<AdPackage> getAll() {
//        log.info("db'den getirildi");
        return adPackageRepository.findAll();
    }

    public AdPackage getById(Long id) {
        Optional<AdPackage> foundAdPackage = adPackageRepository.findById(id);
        if (foundAdPackage.isEmpty()) {
            log.error(ExceptionMessages.PACKET_NOT_FOUND);
            throw new EmlakBuradaException(ExceptionMessages.PACKET_NOT_FOUND);
        }
        return foundAdPackage.get();
    }

}
