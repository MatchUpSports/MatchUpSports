package com.matchUpSports.boundedContext.futsalField.repository;

import com.matchUpSports.boundedContext.futsalField.entity.FutsalField;
import com.matchUpSports.boundedContext.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FutsalFieldRepository extends JpaRepository<FutsalField, Long> {
<<<<<<< HEAD
    // FieldRepository.java
    List<FutsalField> findByFieldLocation(String location);

    FutsalField findByFieldName(String fieldName);

    List<FutsalField> findByMember(Member member);
=======
    Optional<FutsalField> findByIdAndDeleteDateIsNull(Long id);

    @Query("SELECT f FROM FutsalField f WHERE f.member =:member AND f.deleteDate IS NULL")
    List<FutsalField> findByMember(@Param("member") Member member);
>>>>>>> 7599b97 (feat: 시설 수정 삭제 구현 및 baseEntity 추가)
}
