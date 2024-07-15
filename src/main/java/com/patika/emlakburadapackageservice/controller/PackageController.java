package com.patika.emlakburadapackageservice.controller;

import com.patika.emlakburadapackageservice.dto.request.PurchasePackageRequest;
import com.patika.emlakburadapackageservice.dto.response.GenericResponse;
import com.patika.emlakburadapackageservice.service.AdPackageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/packages")
public class PackageController {

    private final AdPackageService packageService;

    @PostMapping
    public ResponseEntity<Void> buyPackage(@RequestBody PurchasePackageRequest request){
        packageService.redirectToPaymentService(request);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("{userId}/check-availability")
    public GenericResponse<Boolean> checkPackageAvailability(@PathVariable Long userId) {
        return GenericResponse.success(packageService.checkPublishingRights(userId));
    }
}
