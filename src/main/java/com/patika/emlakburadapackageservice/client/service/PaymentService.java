package com.patika.emlakburadapackageservice.client.service;

import com.patika.emlakburadapackageservice.client.PaymentClient;
import com.patika.emlakburadapackageservice.client.dto.request.PaymentInformation;
import com.patika.emlakburadapackageservice.client.dto.response.PaymentResponse;
import com.patika.emlakburadapackageservice.dto.request.PackagePaymentRequest;
import com.patika.emlakburadapackageservice.dto.request.PurchasePackageRequest;
import com.patika.emlakburadapackageservice.dto.response.GenericResponse;
import com.patika.emlakburadapackageservice.exception.EmlakBuradaException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class PaymentService {

    private final PaymentClient paymentClient;

    public PaymentResponse purchasePackage(PackagePaymentRequest packagePaymentRequest) {
        GenericResponse<PaymentResponse> response = paymentClient.pay(packagePaymentRequest);

        if (response == null || !HttpStatus.OK.equals(response.getHttpStatus())) {
            String errorMessage = response != null ? response.getMessage() : "Response is null";
            log.error("Error Message: {}", errorMessage);
            throw new EmlakBuradaException("Ödeme alınamadı veya ödeme alınırken bir sorun oluştu.");
        }

        return response.getData();
    }

}