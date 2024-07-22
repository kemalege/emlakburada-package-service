package com.patika.emlakburadapackageservice.service;

import com.patika.emlakburadapackageservice.client.dto.response.PaymentResponse;
import com.patika.emlakburadapackageservice.client.service.PaymentService;
import com.patika.emlakburadapackageservice.consumer.dto.NotificationDto;
import com.patika.emlakburadapackageservice.dto.request.PackagePaymentRequest;
import com.patika.emlakburadapackageservice.dto.request.PaymentDetails;
import com.patika.emlakburadapackageservice.dto.request.PurchasePackageRequest;
import com.patika.emlakburadapackageservice.exception.EmlakBuradaException;
import com.patika.emlakburadapackageservice.exception.ExceptionMessages;
import com.patika.emlakburadapackageservice.model.AdPackage;
import com.patika.emlakburadapackageservice.model.UserPackage;
import com.patika.emlakburadapackageservice.repository.AdPackageRepository;
import org.instancio.Instancio;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdPackageServiceTest {

    @InjectMocks
    private AdPackageService adPackageService;

    @Mock
    private AdPackageRepository adPackageRepository;

    @Mock
    private UserPackageService userPackageService;

    @Mock
    private PaymentService paymentService;

    @Captor
    private ArgumentCaptor<UserPackage> userPackageCaptor;

    @Test
    void redirectToPaymentService_successfully() {

        // given
        PaymentDetails paymentDetails = Instancio.of(PaymentDetails.class)
                .set(field("cardNumber"), "124534234534")
                .set(field("expiryDate"), "11/28")
                .set(field("cvv"), "560")
                .create();

        PurchasePackageRequest request = Instancio.of(PurchasePackageRequest.class)
                .set(field("userId"), 2L)
                .set(field("packageId"), 3L)
                .set(field("paymentDetails"), paymentDetails)
                .create();

        AdPackage adPackage = Instancio.of(AdPackage.class)
                .set(field("id"), 3L)
                .set(field("adCount"), 10)
                .set(field("price"), new BigDecimal("49.99"))
                .set(field("validityDays"), 30)
                .create();

        PaymentResponse paymentResponse = Instancio.of(PaymentResponse.class)
                .set(field("status"), "success")
                .create();

        // Mock behavior
        when(adPackageRepository.findById(3L)).thenReturn(Optional.of(adPackage));
        when(paymentService.purchasePackage(any(PackagePaymentRequest.class))).thenReturn(paymentResponse);

        // when
        adPackageService.redirectToPaymentService(request);

        // then
        verify(paymentService, times(1)).purchasePackage(any(PackagePaymentRequest.class));
        verify(adPackageRepository, times(1)).findById(3L);
    }

    @Test
    void assignPackageToUser_newPackage() {
        // given
        NotificationDto notificationDto = Instancio.of(NotificationDto.class)
                .set(field("packageId"), 2L)
                .set(field("userId"), 3L)
                .create();

        AdPackage adPackage = Instancio.of(AdPackage.class)
                .set(field("id"), notificationDto.getPackageId())
                .set(field("adCount"), 10)
                .set(field("price"), new BigDecimal("49.99"))
                .set(field("validityDays"), 30)
                .create();

        // Mock behavior
        when(adPackageRepository.findById(notificationDto.getPackageId())).thenReturn(Optional.of(adPackage));
        when(userPackageService.getPackagesById(notificationDto.getUserId())).thenReturn(Collections.emptyList());

        // when
        adPackageService.assignPackageToUser(notificationDto);

        // then
        verify(adPackageRepository, times(1)).findById(notificationDto.getPackageId());
        verify(userPackageService, times(1)).getPackagesById(notificationDto.getUserId());
        verify(userPackageService, times(1)).save(userPackageCaptor.capture());

        UserPackage capturedUserPackage = userPackageCaptor.getValue();
        assertThat(capturedUserPackage.getUserId()).isEqualTo(notificationDto.getUserId());
        assertThat(capturedUserPackage.getPackageId()).isEqualTo(notificationDto.getPackageId());
        assertThat(capturedUserPackage.getRemainingCount()).isEqualTo(10);
        assertThat(capturedUserPackage.getExpiryDate()).isEqualToIgnoringSeconds(LocalDateTime.now().plusDays(30));
    }

    @Test
    void assignPackageToUser_extendExistingPackage() {
        // given
        NotificationDto notificationDto = Instancio.of(NotificationDto.class)
                .set(field("packageId"), 2L)
                .set(field("userId"), 3L)
                .create();

        AdPackage adPackage = Instancio.of(AdPackage.class)
                .set(field("id"), notificationDto.getPackageId())
                .set(field("adCount"), 10)
                .set(field("price"), new BigDecimal("49.99"))
                .set(field("validityDays"), 30)
                .create();

        LocalDateTime referenceTime = LocalDateTime.of(2024, 8, 26, 18, 17, 58, 298482100);

        UserPackage existingUserPackage = Instancio.of(UserPackage.class)
                .set(field("userId"), notificationDto.getUserId())
                .set(field("packageId"), notificationDto.getPackageId())
                .set(field("remainingCount"), 5)
                .set(field("expiryDate"), referenceTime)
                .create();

        List<UserPackage> userPackages = Collections.singletonList(existingUserPackage);

        // Mock behavior
        when(adPackageRepository.findById(notificationDto.getPackageId())).thenReturn(Optional.of(adPackage));
        when(userPackageService.getPackagesById(notificationDto.getUserId())).thenReturn(userPackages);

        // when
        adPackageService.assignPackageToUser(notificationDto);

        // then
        verify(adPackageRepository, times(1)).findById(notificationDto.getPackageId());
        verify(userPackageService, times(1)).getPackagesById(notificationDto.getUserId());
        verify(userPackageService, times(1)).save(userPackageCaptor.capture());

        LocalDateTime expectedExpiryDate = referenceTime.plusDays(adPackage.getValidityDays());

        UserPackage capturedUserPackage = userPackageCaptor.getValue();
        assertThat(capturedUserPackage.getUserId()).isEqualTo(notificationDto.getUserId());
        assertThat(capturedUserPackage.getPackageId()).isEqualTo(notificationDto.getPackageId());
        assertThat(capturedUserPackage.getRemainingCount()).isEqualTo(15);
        assertThat(capturedUserPackage.getExpiryDate()).isEqualTo(expectedExpiryDate);
    }

    @Test
    void extendExistingPackage() {
        // given
        AdPackage adPackageToBuy = Instancio.of(AdPackage.class)
                .set(field("id"), 1L)
                .set(field("adCount"), 10)
                .set(field("price"), new BigDecimal("49.99"))
                .set(field("validityDays"), 30)
                .create();

        UserPackage userPackage1 = Instancio.of(UserPackage.class)
                .set(field("id"), 1L)
                .set(field("expiryDate"), LocalDateTime.now().plusDays(10))
                .set(field("remainingCount"), 5)
                .create();

        UserPackage userPackage2 = Instancio.of(UserPackage.class)
                .set(field("id"), 2L)
                .set(field("expiryDate"), LocalDateTime.now().plusDays(20))
                .set(field("remainingCount"), 15)
                .create();

        List<UserPackage> userPackages = Arrays.asList(userPackage1, userPackage2);

        // expected values
        LocalDateTime expectedExpiryDate = userPackage2.getExpiryDate().plusDays(adPackageToBuy.getValidityDays());
        int expectedRemainingCount = userPackage2.getRemainingCount() + adPackageToBuy.getAdCount();

        // when
        adPackageService.extendExistingPackage(userPackages, adPackageToBuy);

        // then
        verify(userPackageService, times(1)).save(userPackageCaptor.capture());

        UserPackage updatedUserPackage = userPackageCaptor.getValue();
        assertThat(updatedUserPackage.getId()).isEqualTo(userPackage2.getId());
        assertThat(updatedUserPackage.getExpiryDate()).isEqualTo(expectedExpiryDate);
        assertThat(updatedUserPackage.getRemainingCount()).isEqualTo(expectedRemainingCount);
    }

    @Test
    void checkPublishingRights_packageExpired() {
        // given
        Long userId = 1L;

        UserPackage expiredPackage = Instancio.of(UserPackage.class)
                .set(field("expiryDate"), LocalDateTime.now().minusDays(1))
                .set(field("remainingCount"), 10)
                .create();

        List<UserPackage> userPackages = Collections.singletonList(expiredPackage);

        // Mock behavior
        when(userPackageService.getPackagesById(userId)).thenReturn(userPackages);

        // when / then
        Exception exception = Assertions.assertThrows(EmlakBuradaException.class, () -> {
            adPackageService.checkPublishingRights(userId);
        });

        assertThat(exception.getMessage()).isEqualTo(ExceptionMessages.PACKET_EXPIRED);
    }

    @Test
    void checkPublishingRights_insufficientAdCount() {
        // given
        Long userId = 1L;

        UserPackage insufficientAdCountPackage = Instancio.of(UserPackage.class)
                .set(field("expiryDate"), LocalDateTime.now().plusDays(1))
                .set(field("remainingCount"), 0)
                .create();

        List<UserPackage> userPackages = Collections.singletonList(insufficientAdCountPackage);

        // Mock behavior
        when(userPackageService.getPackagesById(userId)).thenReturn(userPackages);

        // when / then
        Exception exception = Assertions.assertThrows(EmlakBuradaException.class, () -> {
            adPackageService.checkPublishingRights(userId);
        });

        assertThat(exception.getMessage()).isEqualTo(ExceptionMessages.INSUFFICIENT_AD_COUNT);
    }

    @Test
    void checkPublishingRights_success() {
        // given
        Long userId = 1L;

        UserPackage validPackage = Instancio.of(UserPackage.class)
                .set(field("expiryDate"), LocalDateTime.now().plusDays(1))
                .set(field("remainingCount"), 10)
                .create();

        List<UserPackage> userPackages = Collections.singletonList(validPackage);

        // Mock behavior
        when(userPackageService.getPackagesById(userId)).thenReturn(userPackages);

        // when
        Boolean result = adPackageService.checkPublishingRights(userId);

        // then
        Assertions.assertTrue(result);
    }

    @Test
    void getAll_shouldReturnAllAdPackages() {
        // given
        AdPackage adPackage1 = Instancio.of(AdPackage.class)
                .set(field("id"), 1L)
                .set(field("adCount"), 10)
                .set(field("price"), new BigDecimal("49.99"))
                .set(field("validityDays"), 30)
                .create();

        AdPackage adPackage2 = Instancio.of(AdPackage.class)
                .set(field("id"), 2L)
                .set(field("adCount"), 20)
                .set(field("price"), new BigDecimal("99.99"))
                .set(field("validityDays"), 60)
                .create();

        List<AdPackage> adPackages = Arrays.asList(adPackage1, adPackage2);

        when(adPackageRepository.findAll()).thenReturn(adPackages);

        // when
        List<AdPackage> result = adPackageService.getAll();

        // then
        verify(adPackageRepository, times(1)).findAll();
        assertThat(result).isEqualTo(adPackages);
    }

    @Test
    void shouldThrowException_whenAdpackageIsNotFound() {

        PurchasePackageRequest request = Instancio.of(PurchasePackageRequest.class).create();

        //when
        EmlakBuradaException emlakBuradaException = Assertions.assertThrows(EmlakBuradaException.class, () -> adPackageService.redirectToPaymentService(request));

        assertThat(emlakBuradaException.getMessage()).isEqualTo("Paket bulunamadı.");

        verifyNoInteractions(paymentService);

    }


}
