package com.matchUpSports.boundedContext.futsalField.service;

import com.matchUpSports.boundedContext.futsalField.entity.FutsalField;
import com.matchUpSports.boundedContext.futsalField.form.CreateFutsalFieldForm;
import com.matchUpSports.boundedContext.futsalField.repository.FutsalFieldImageRepository;
import com.matchUpSports.boundedContext.futsalField.repository.FutsalFieldRepository;
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
public class FutsalFieldService {
    private final FutsalFieldRepository fieldRepository;
    private final FutsalFieldImageService fieldImageService;
    private final FutsalFieldImageRepository fieldImageRepository;

    public List<FutsalField> findAll() {
        return fieldRepository.findAll();
    }

    @Transactional
    public void create(@Valid CreateFutsalFieldForm createForm) {
        try {
            FutsalField field = FutsalField.builder()
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

    //해당 지역에 있는 스타디움 찾는 로직
    public List<Field> findFieldsByLocation(String location) {
        if (location == null || location.isEmpty()) {
            return fieldRepository.findAll();
        }
        return fieldRepository.findByFieldLocation(location);
    }

}