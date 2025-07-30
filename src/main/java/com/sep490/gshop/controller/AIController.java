package com.sep490.gshop.controller;

import com.sep490.gshop.common.constants.URLConstant;
import com.sep490.gshop.payload.response.RawDataResponse;
import com.sep490.gshop.service.AIService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

@RestController
@RequestMapping(URLConstant.AI)
@Log4j2
@CrossOrigin("*")
public class AIController {

    private final AIService aiService;

    @Autowired
    public AIController(AIService aiService) {
        this.aiService = aiService;
    }

    @GetMapping("/test-chat")
    public String chat(String message) {
        log.info("chat() AIController start | message: {}", message);
        String response = aiService.chat(message);
        log.info("chat() AIController end | response: {}", response);
        return response;
    }

    @GetMapping("get-raw-data")
    public ResponseEntity<RawDataResponse> getRawData(@RequestParam("link") @NotNull String link) {
        log.info("getRawData() AIController start");
        RawDataResponse rawData = aiService.getRawData(link);
        log.info("getRawData() AIController end | rawData: {}", rawData.getName());
        return ResponseEntity.ok(rawData);
    }
}
