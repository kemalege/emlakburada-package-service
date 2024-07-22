package com.patika.emlakburadapackageservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.patika.emlakburadapackageservice.consumer.dto.NotificationDto;
import com.patika.emlakburadapackageservice.dto.request.PaymentDetails;
import com.patika.emlakburadapackageservice.dto.request.PurchasePackageRequest;
import com.patika.emlakburadapackageservice.model.AdPackage;
import com.patika.emlakburadapackageservice.model.UserPackage;
import com.patika.emlakburadapackageservice.service.AdPackageService;
import com.patika.emlakburadapackageservice.service.UserPackageService;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.instancio.Select.field;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PackageController.class)
class PackageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdPackageService adPackageService;

    @MockBean
    private UserPackageService userPackageService;

    @Test
    void buyPackage() throws Exception {

        //given
        ObjectMapper objectMapper = new ObjectMapper();
        String body = objectMapper.writeValueAsString(preparePackageBuyRequest());

        //when
        ResultActions resultActions = mockMvc.perform(post("/api/v1/packages")
                .content(body)
                .contentType(MediaType.APPLICATION_JSON));

        //then - assertion
        resultActions.andExpect(status().isCreated());
        verify(adPackageService, times(1)).redirectToPaymentService(Mockito.any(PurchasePackageRequest.class));
    }

    @Test
    void checkPackageAvailability() throws Exception {

        Long userId = 2L;
        Boolean availability = true;

        given(adPackageService.checkPublishingRights(userId)).willReturn(availability);

        mockMvc.perform(get("/api/v1/packages/{userId}/check-availability", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(availability));

        verify(adPackageService, times(1)).checkPublishingRights(userId);
    }

    @Test
    void updatePackageRights() throws Exception {
        Long userId = 2L;

        ResultActions resultActions = mockMvc.perform(post("/api/v1/packages/{userId}/update-rights", userId)
                .contentType(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isOk());
        verify(userPackageService, times(1)).decrementPackageRights(userId);
    }

    @Test
    void getUserPackagesById() throws Exception {

        Long userId = 2L;

        List<UserPackage> response = prepareUserPackageResponse();

        given(userPackageService.getPackagesById(userId)).willReturn(response);

        mockMvc.perform(get("/api/v1/packages/user/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(6))
                .andExpect(jsonPath("$.data[0].userId").value(2))
                .andExpect(jsonPath("$.data[0].packageId").value(4))
                .andExpect(jsonPath("$.data[0].remainingCount").value(138))
                .andExpect(jsonPath("$.data[0].expiryDate").value("2025-02-20T03:06:00"));

        verify(userPackageService, times(1)).getPackagesById(userId);
    }

    @Test
    void getAdPackagesById() throws Exception {

        Long AdId = 2L;

        AdPackage response = Instancio.of(AdPackage.class)
                .create();

        given(adPackageService.getById(AdId)).willReturn(response);

        mockMvc.perform(get("/api/v1/packages/{id}", AdId)
                        .contentType(MediaType.APPLICATION_JSON));

        verify(adPackageService, times(1)).getById(AdId);
    }

    @Test
    void getAllAdPackages() throws Exception {

        List<AdPackage> response = prepareAdPackageResponse();

        given(adPackageService.getAll()).willReturn(response);

        mockMvc.perform(get("/api/v1/packages")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(adPackageService, times(1)).getAll();
    }

    private PurchasePackageRequest preparePackageBuyRequest() {
        PurchasePackageRequest request = new PurchasePackageRequest();
        PaymentDetails paymentDetails = preparePaymentDetails();
        request.setPackageId(1L);
        request.setUserId(2L);
        request.setPaymentDetails(paymentDetails);
        return request;
    }

    private PaymentDetails preparePaymentDetails() {
        PaymentDetails request = new PaymentDetails();
        request.setCardNumber("2342564556534532");
        request.setCvv("345");
        request.setExpiryDate("11/20");
        return request;
    }

    private List<UserPackage> prepareUserPackageResponse() {
        UserPackage userPackage = new UserPackage();
        userPackage.setId(6L);
        userPackage.setUserId(2L);
        userPackage.setPackageId(4L);
        userPackage.setPackageId(4L);
        userPackage.setRemainingCount(138);
        userPackage.setExpiryDate(LocalDateTime.of(2025, 2, 20, 3, 6));

        return List.of(userPackage);
    }

    private List<AdPackage> prepareAdPackageResponse() {
        AdPackage adPackage = new AdPackage();
        adPackage.setId(2L);
                adPackage.setAdCount(10);
                adPackage.setPrice(new BigDecimal(40));
                adPackage.setValidityDays(30);
        return List.of(adPackage);
    }

}
