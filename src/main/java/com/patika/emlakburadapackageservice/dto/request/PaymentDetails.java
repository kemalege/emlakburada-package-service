package com.patika.emlakburadapackageservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDetails {

    private String cardNumber;
    private String expiryDate;
    private String cvv;

}

