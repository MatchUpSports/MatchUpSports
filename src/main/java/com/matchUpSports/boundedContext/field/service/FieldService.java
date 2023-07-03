package com.matchUpSports.boundedContext.field.service;

import com.matchUpSports.boundedContext.field.entity.Field;
import com.matchUpSports.boundedContext.field.form.CreateFieldForm;
import com.matchUpSports.boundedContext.field.repository.FieldImageRepository;
import com.matchUpSports.boundedContext.field.repository.FieldRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FieldService {
    private final FieldRepository fieldRepository;
    private final FieldImageService fieldImageService;
    private final FieldImageRepository fieldImageRepository;

    @Transactional
    public void create(@Valid CreateFieldForm createForm) {
        try {
            Field field = Field.builder()
                    .fieldName(createForm.getName())
                    .fieldLocation(createForm.getLocation())
                    .openTime(createForm.getOpenTime())
                    .closeTime(createForm.getCloseTime())
                    .courtCount(createForm.getCount())
                    .price(createForm.getPrice())
                    .registNum(createForm.getRegistNum())
                    .build();

            fieldRepository.save(field);

            List<MultipartFile> images = createForm.getImages();

            for (MultipartFile image : images) {
                if (image.isEmpty()) {
                    log.info("이미지 뺴고 성공~!");
                }
            }

            fieldImageService.uploadImages(field.getId(), images);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
