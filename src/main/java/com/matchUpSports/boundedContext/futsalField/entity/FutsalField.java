package com.matchUpSports.boundedContext.field.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalTime;

@Entity
@Getter
@Setter
@SuperBuilder
public class Field {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fieldName;

    private String fieldLocation;

    private int price;

    private LocalTime openTime;

    private LocalTime closeTime;

    private int courtCount;

    private String registNum;

    public Field() {
        this.fieldName = null;
        this.fieldLocation = null;
        this.price = 0;
        this.closeTime = LocalTime.now();
        this.openTime = LocalTime.now();
        this.courtCount = 0;
        this.registNum = null;
    }
}
