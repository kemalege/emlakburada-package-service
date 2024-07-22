package com.patika.emlakburadapackageservice.controller;

import com.patika.emlakburadapackageservice.dto.request.PurchasePackageRequest;
import com.patika.emlakburadapackageservice.dto.response.GenericResponse;
import com.patika.emlakburadapackageservice.model.AdPackage;
import com.patika.emlakburadapackageservice.model.UserPackage;
import com.patika.emlakburadapackageservice.service.AdPackageService;
import com.patika.emlakburadapackageservice.service.UserPackageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/packages")
public class PackageController {

    private final AdPackageService adPackageService;
    private final UserPackageService userPackageService;

    @PostMapping
    public ResponseEntity<Void> buyPackage(@RequestBody PurchasePackageRequest request){
        adPackageService.redirectToPaymentService(request);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("{userId}/check-availability")
    public GenericResponse<Boolean> checkPackageAvailability(@PathVariable Long userId) {
        return GenericResponse.success(adPackageService.checkPublishingRights(userId));
    }

    @PostMapping("{userId}/update-rights")
    public ResponseEntity<Void> updatePackageRights(@PathVariable Long userId) {
        userPackageService.decrementPackageRights(userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("user/{id}")
    public GenericResponse<List<UserPackage>> getUserPackagesById(@PathVariable Long id) {
        return GenericResponse.success(userPackageService.getPackagesById(id));
    }

    @GetMapping
    public GenericResponse<List<AdPackage>> getAllAdPackages() {
        return GenericResponse.success(adPackageService.getAll());
    }

    @GetMapping("{id}")
    public GenericResponse<AdPackage> getAdPackageById(@PathVariable Long id) {
        return GenericResponse.success(adPackageService.getById(id));
    }
}
