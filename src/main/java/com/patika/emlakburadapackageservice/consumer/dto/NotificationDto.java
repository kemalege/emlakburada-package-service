package com.patika.emlakburadapackageservice.consumer.dto;

import com.patika.emlakburadapackageservice.consumer.dto.enums.NotificationType;
import com.patika.emlakburadapackageservice.consumer.dto.enums.PaymentStatus;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class NotificationDto {

    private NotificationType notificationType;
    private Long packageId;
    private Long userId;

}
