package com.patika.emlakburadapackageservice.exception;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExceptionMessages {
    public static final String PACKET_NOT_FOUND = "Paket bulunamadı.";
    public static final String INSUFFICIENT_AD_COUNT = "Yayınlama hakkınız dolmuştur.";
    public static final String PACKET_EXPIRED = "Paketin geçerlilik süresi dolmuştur.";
}
