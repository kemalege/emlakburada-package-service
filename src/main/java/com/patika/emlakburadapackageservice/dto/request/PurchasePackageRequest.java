package com.patika.emlakburadapackageservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PurchasePackageRequest {

    private Long userId;
    private Long packageId;
    private PaymentDetails paymentDetails;

}
