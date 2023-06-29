package com.matchUpSports.boundedContext.field.repository;

import com.matchUpSports.boundedContext.field.entity.Field;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FieldRepository extends JpaRepository<Field, Long> {
}
