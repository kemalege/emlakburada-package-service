package com.patika.emlakburadapackageservice.service;

import com.patika.emlakburadapackageservice.model.UserPackage;
import com.patika.emlakburadapackageservice.repository.AdPackageRepository;
import com.patika.emlakburadapackageservice.repository.UserPackageRepository;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserPackageServiceTest {

    @InjectMocks
    private UserPackageService userPackageService;

    @Mock
    private AdPackageRepository adPackageRepository;

    @Mock
    private UserPackageRepository userPackageRepository;

    @Captor
    private ArgumentCaptor<UserPackage> userPackageCaptor;

    @Test
    void saveUserPackage_successfuly() {
        // given
        UserPackage userPackage = Instancio.of(UserPackage.class).create();

        // when
        userPackageService.save(userPackage);

        // then
        verify(userPackageRepository, times(1)).save(userPackage);
    }

    @Test
    void getPackagesById_successfuly() {
        // given
        Long userId = 1L;
        UserPackage userPackage = Instancio.of(UserPackage.class)
                .set(field("userId"), userId)
                .create();
        List<UserPackage> userPackages = Collections.singletonList(userPackage);

        when(userPackageRepository.findAllByUserId(userId)).thenReturn(userPackages);

        // when
        List<UserPackage> result = userPackageService.getPackagesById(userId);

        // then
        verify(userPackageRepository, times(1)).findAllByUserId(userId);
        assertThat(result).isEqualTo(userPackages);
    }

    @Test
    void decrementPackageRights_validPackage() {
        // given
        Long userId = 1L;
        UserPackage validPackage = Instancio.of(UserPackage.class)
                .set(field("userId"), userId)
                .set(field("expiryDate"), LocalDateTime.now().plusDays(10))
                .set(field("remainingCount"), 5)
                .create();

        List<UserPackage> userPackages = Collections.singletonList(validPackage);

        when(userPackageRepository.findAllByUserId(userId)).thenReturn(userPackages);

        // when
        userPackageService.decrementPackageRights(userId);

        // then
        verify(userPackageRepository, times(1)).findAllByUserId(userId);
        verify(userPackageRepository, times(1)).save(validPackage);
        assertThat(validPackage.getRemainingCount()).isEqualTo(4);
    }

    @Test
    void decrementPackageRights_noValidPackage() {
        // given
        Long userId = 1L;
        UserPackage expiredPackage = Instancio.of(UserPackage.class)
                .set(field("userId"), userId)
                .set(field("expiryDate"), LocalDateTime.now().minusDays(1))
                .set(field("remainingCount"), 5)
                .create();

        List<UserPackage> userPackages = Collections.singletonList(expiredPackage);

        when(userPackageRepository.findAllByUserId(userId)).thenReturn(userPackages);

        // when
        userPackageService.decrementPackageRights(userId);

        // then
        verify(userPackageRepository, times(1)).findAllByUserId(userId);
        verify(userPackageRepository, never()).save(any(UserPackage.class));
        assertThat(expiredPackage.getRemainingCount()).isEqualTo(5);
    }

    @Test
    void decrementPackageRights_noRemainingCount() {
        // given
        Long userId = 1L;
        UserPackage noRemainingCountPackage = Instancio.of(UserPackage.class)
                .set(field("userId"), userId)
                .set(field("expiryDate"), LocalDateTime.now().plusDays(10))
                .set(field("remainingCount"), 0)
                .create();

        List<UserPackage> userPackages = Collections.singletonList(noRemainingCountPackage);

        when(userPackageRepository.findAllByUserId(userId)).thenReturn(userPackages);

        // when
        userPackageService.decrementPackageRights(userId);

        // then
        verify(userPackageRepository, times(1)).findAllByUserId(userId);
        verify(userPackageRepository, never()).save(any(UserPackage.class));
        assertThat(noRemainingCountPackage.getRemainingCount()).isEqualTo(0);
    }

}
