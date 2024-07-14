package com.patika.emlakburadapackageservice.service;

import com.patika.emlakburadapackageservice.client.dto.response.PaymentResponse;
import com.patika.emlakburadapackageservice.client.service.PaymentService;
import com.patika.emlakburadapackageservice.consumer.dto.NotificationDto;
import com.patika.emlakburadapackageservice.converter.PackagePaymentRequestConverter;
import com.patika.emlakburadapackageservice.converter.UserPackageConverter;
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
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class AdPackageService {

    private final PaymentService paymentService;
    private final AdPackageRepository adPackageRepository;
    private final UserPackageRepository userPackageRepository;

    public void redirectToPaymentService(PurchasePackageRequest request) {

        AdPackage adPackage = getById(request.getPackageId());

        log.info("found adPackage: {}", adPackage.toString());

        com.patika.emlakburadapackageservice.dto.request.PackagePaymentRequest packagePaymentRequest = PackagePaymentRequestConverter.convert(request, adPackage.getPrice());

        PaymentResponse paymentResponse = paymentService.purchasePackage(packagePaymentRequest);

        log.info("payment status. {}", paymentResponse.getStatus());

    }

    @RabbitListener(queues = "${notification.queue}")
    public void assignPackageToUser(NotificationDto notificationDto) {
        log.info("Received payment details :{}", notificationDto.toString());

        AdPackage adPackage = getById(notificationDto.getPackageId());

        UserPackage userPackage = UserPackageConverter.convert(notificationDto, adPackage);

        userPackageRepository.save(userPackage);

        log.info("Package assigned to user: {}", notificationDto.getUserId());

    }

    public AdPackage getById(Long id) {
        Optional<AdPackage> foundAd = adPackageRepository.findById(id);
        if (foundAd.isEmpty()) {
            log.error(ExceptionMessages.PACKET_NOT_FOUND);
            throw new EmlakBuradaException(ExceptionMessages.PACKET_NOT_FOUND);
        }
        return foundAd.get();
    }
}
