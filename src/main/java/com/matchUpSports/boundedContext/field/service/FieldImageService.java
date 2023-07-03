package com.matchUpSports.boundedContext.field.service;

import com.matchUpSports.boundedContext.field.entity.Field;
import com.matchUpSports.boundedContext.field.entity.FieldImage;
import com.matchUpSports.boundedContext.field.repository.FieldImageRepository;
import com.matchUpSports.boundedContext.field.repository.FieldRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FieldImageService {
    private final FieldRepository fieldRepository;
    private final FieldImageRepository fieldImageRepository;

    @Value("${spring.dev_fileLocation}")
    private String storageLocation;

    // 시설 사진 등록
    public void uploadImages(Long id, List<MultipartFile> images) throws IOException {
        Field field = fieldRepository.findById(id).orElse(null);

        List<FieldImage> fieldImages = new ArrayList<>();

        for(MultipartFile image: images) {
            String fileName = generatedUniqueFileName(image.getOriginalFilename());
            String filePath = storageLocation + fileName;

            log.info("fieldImage 확인 = {}", fileName);

            image.transferTo(new File(filePath));

            FieldImage fieldImage = FieldImage
                    .builder()
                    .field(field)
                    .name(fileName)
                    .path(filePath)
                    .build();

            fieldImages.add(fieldImage);
        }

        fieldImageRepository.saveAll(fieldImages);

    }

    private String generatedUniqueFileName(String originalFilename) {
        String extension = FilenameUtils.getExtension(originalFilename);
        String uniqueFileName = UUID.randomUUID() + "." + extension;

        return uniqueFileName;
    }
}
