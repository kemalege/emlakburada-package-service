package com.patika.emlakburadapackageservice.client.dto.request;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentInformation {

    private String cardNumber;
    private String expiryDate;
    private String cvv;
    private BigDecimal amount;
}

