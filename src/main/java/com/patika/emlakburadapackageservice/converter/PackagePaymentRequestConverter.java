package com.patika.emlakburadapackageservice.converter;

import com.patika.emlakburadapackageservice.dto.request.PackagePaymentRequest;
import com.patika.emlakburadapackageservice.dto.request.PurchasePackageRequest;

import java.math.BigDecimal;

public class PackagePaymentRequestConverter {

    public static PackagePaymentRequest convert(PurchasePackageRequest request, BigDecimal amount) {
        return PackagePaymentRequest.builder()
                .purchasePackageRequest(request)
                .amount(amount)
                .build();
    }
}

