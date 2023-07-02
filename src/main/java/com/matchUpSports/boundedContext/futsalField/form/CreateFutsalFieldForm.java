package com.matchUpSports.boundedContext.futsalField.form;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalTime;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class CreateFutsalFieldForm {

    @NotEmpty(message = "이름을 입력해 주세요.")
    private String name;

    @NotEmpty(message = "시설의 위치를 입력해 주세요.")
    private String location;

    @NotNull(message = "시설 이용료를 입력해 주세요.")
    private int price;

    @NotNull(message = "시설을 여는 시간을 입력해 주세요.")
    private LocalTime openTime;

    @NotNull(message = "시설을 닫는 시간을 입력해 주세요.")
    private LocalTime closeTime;

    private List<MultipartFile> images;

    @NotNull(message = "구장의 개수를 입력해 주세요.")
    private int count;

    @NotEmpty(message = "사업자 등록 번호를 입력해주세요.")
    private String registNum;

    public CreateFutsalFieldForm() {
        this.images = null;
    }

}
