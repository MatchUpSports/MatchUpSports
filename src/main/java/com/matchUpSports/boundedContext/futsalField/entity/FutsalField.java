package com.matchUpSports.boundedContext.futsalField.entity;

import com.matchUpSports.base.entity.BaseEntity;
import com.matchUpSports.boundedContext.member.entity.Member;
import jakarta.persistence.*;
import com.matchUpSports.boundedContext.member.entity.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalTime;

@Entity
@Getter
@Setter
@SuperBuilder(toBuilder = true)
public class FutsalField extends BaseEntity {
    @ManyToOne
    @JoinColumn(name="futsalFields")
    private Member member;

    private String fieldName;

    private String fieldLocation;

    private int price;

    private LocalTime openTime;

    private LocalTime closeTime;

    private int courtCount;

    private String registNum;

//    private  String imageUrl;

    public FutsalField() {
        this.fieldName = null;
        this.fieldLocation = null;
        this.price = 0;
        this.closeTime = LocalTime.now();
        this.openTime = LocalTime.now();
        this.courtCount = 0;
        this.registNum = null;
        this.member = null;
    }
}
