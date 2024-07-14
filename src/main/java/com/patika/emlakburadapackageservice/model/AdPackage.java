package com.patika.emlakburadapackageservice.model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "packages")
public class AdPackage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ad_count", nullable = false)
    private int adCount;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "validity_days", nullable = false)
    private int validityDays;

}
