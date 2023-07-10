package com.matchUpSports.boundedContext.futsalField.repository;

import com.matchUpSports.boundedContext.futsalField.entity.FutsalField;
import com.matchUpSports.boundedContext.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FutsalFieldRepository extends JpaRepository<FutsalField, Long> {
    // FieldRepository.java
    List<FutsalField> findByFieldLocation(String location);

    FutsalField findByFieldName(String fieldName);

    Optional<FutsalField> findByIdAndDeleteDateIsNull(Long id);

    @Query("SELECT f FROM FutsalField f WHERE f.member =:member AND f.deleteDate IS NULL")
    List<FutsalField> findByMember(@Param("member") Member member);
}
