package com.matchUpSports.boundedContext.field.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Getter
@Setter
public class Field {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fieldName;

    private String fieldLocation;

    private Long price;

    private LocalTime openTime;

    private LocalTime closeTime;

    private int courtCount;

    private String image;

    private String registNum;

}
