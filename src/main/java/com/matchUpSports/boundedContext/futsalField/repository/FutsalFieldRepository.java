package com.matchUpSports.boundedContext.futsalField.repository;

import com.matchUpSports.boundedContext.futsalField.entity.FutsalField;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FutsalFieldRepository extends JpaRepository<FutsalField, Long> {
    // FieldRepository.java
    List<FutsalField> findByFieldLocation(String location);

    FutsalField findByFieldName(String fieldName);

}
