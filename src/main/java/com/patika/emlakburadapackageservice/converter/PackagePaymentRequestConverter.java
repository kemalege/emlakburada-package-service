package com.patika.emlakburadapackageservice.converter;

import com.patika.emlakburadapackageservice.dto.request.PurchasePackageRequest;

import java.math.BigDecimal;

public class PackagePaymentRequestConverter {

    public static com.patika.emlakburadapackageservice.dto.request.PackagePaymentRequest convert(PurchasePackageRequest request, BigDecimal amount) {
        return com.patika.emlakburadapackageservice.dto.request.PackagePaymentRequest.builder()
                .purchasePackageRequest(request)
                .amount(amount)
                .build();
    }
}

