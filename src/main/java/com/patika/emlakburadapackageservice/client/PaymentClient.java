package com.patika.emlakburadapackageservice.client;

import com.patika.emlakburadapackageservice.client.dto.response.PaymentResponse;
import com.patika.emlakburadapackageservice.dto.request.PackagePaymentRequest;
import com.patika.emlakburadapackageservice.dto.response.GenericResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "payment-service", url = "localhost:8099/api/v1/pay")
public interface PaymentClient {

    @PostMapping
    GenericResponse<PaymentResponse> pay(@RequestBody PackagePaymentRequest request);

}
