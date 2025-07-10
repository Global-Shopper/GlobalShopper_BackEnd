package com.sep490.gshop.controller;

import com.sep490.gshop.common.constants.URLConstant;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping(URLConstant.HEALTH_CHECK)
@Log4j2
public class HealthCheckController {
    @GetMapping
    public String healthCheck() {
        log.info("Health Check Started");
        Date currentDate = new Date();
        String message = "Health Check at " + currentDate + ": OK!";
        log.info("Health Check End: {}", currentDate);
        return message;
    }
}
