package com.matchUpSports.boundedContext.futsalField.dto;

import com.matchUpSports.boundedContext.futsalField.entity.FutsalField;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FutsalFieldModifyDto {
    @NotBlank
    private String name;
    @NotBlank
    private String location;
    @NotBlank
    private int price;
    @NotBlank
    private LocalTime openTime;
    @NotBlank
    private LocalTime closeTime;
    @NotBlank
    private int courtCount;

//    private  String imageUrl;

    public boolean isSame(FutsalField futsalField) {
        return name.equals(futsalField.getFieldName()) && (Objects.equals(location, futsalField.getFieldLocation())) && (price == (futsalField.getPrice())) &&
                openTime.equals(futsalField.getOpenTime()) && closeTime.equals(futsalField.getCloseTime()) &&
                (courtCount == (futsalField.getCourtCount()));
    }
//    public boolean isSame(FutsalField futsalField) {
//        return name.equalsIgnoreCase(futsalField.getFieldName()) &&
//                location.equals(futsalField.getFieldLocation()) &&
//                price.equals(futsalField.getPrice()) &&
//                openTime.equals(futsalField.getOpenTime()) &&
//                closeTime.equals(futsalField.getCloseTime()) &&
//                courtCount.equals(futsalField.getCourtCount());
//    }
}
