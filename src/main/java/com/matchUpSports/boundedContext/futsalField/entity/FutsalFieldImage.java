package com.matchUpSports.boundedContext.futsalField.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@EntityListeners(AuditingEntityListener.class)
public class FutsalFieldImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    private String name;

    private String path;

    @ManyToOne(fetch = FetchType.LAZY)
    private FutsalField field;
}
