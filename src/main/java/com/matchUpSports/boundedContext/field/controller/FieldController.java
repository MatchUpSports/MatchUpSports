package com.matchUpSports.boundedContext.field.controller;

import com.matchUpSports.boundedContext.field.service.FieldService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/field")
public class FieldController {
    private final FieldService fieldService;

}
