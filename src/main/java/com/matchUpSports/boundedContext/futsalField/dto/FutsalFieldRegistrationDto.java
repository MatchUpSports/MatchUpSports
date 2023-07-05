package com.matchUpSports.boundedContext.futsalField.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FutsalFieldRegistrationDto {

    private String name;

    private String location;

    private int price;

    private LocalTime openTime;

    private LocalTime closeTime;

    private int courtCount;

    private String registNum;

//    private  String imageUrl;
}
