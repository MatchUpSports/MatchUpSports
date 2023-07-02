package com.matchUpSports.boundedContext.futsalField.service;

import com.matchUpSports.boundedContext.futsalField.entity.FutsalField;
import com.matchUpSports.boundedContext.futsalField.entity.FutsalFieldImage;
import com.matchUpSports.boundedContext.futsalField.repository.FutsalFieldImageRepository;
import com.matchUpSports.boundedContext.futsalField.repository.FutsalFieldRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
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
public class FutsalFieldImageService {
    private final FutsalFieldRepository fieldRepository;
    private final FutsalFieldImageRepository fieldImageRepository;

    @Value("${spring.dev_fileLocation}")
    private String storageLocation;

    // 시설 사진 등록
    public void uploadImages(Long id, List<MultipartFile> images) throws IOException {
        FutsalField field = fieldRepository.findById(id).orElse(null);

        List<FutsalFieldImage> fieldImages = new ArrayList<>();

        for (MultipartFile image : images) {
            String fileName = generatedUniqueFileName(image.getOriginalFilename());
            String filePath = storageLocation + fileName;

            log.info("fieldImage 확인 = {}", fileName);

            image.transferTo(new File(filePath));

            FutsalFieldImage fieldImage = FutsalFieldImage
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
