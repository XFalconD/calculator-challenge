package com.wit.rest.controller;

import java.math.BigDecimal;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wit.rest.dto.ApiResponse;
import com.wit.rest.service.CalculatorService;
import com.wit.rest.util.MDCUtil;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class CalculatorController {

    private final CalculatorService calculatorService;
    private final MDCUtil mdcUtil;

    public CalculatorController(CalculatorService calculatorService, MDCUtil mdcUtil) {
        this.calculatorService = calculatorService;
        this.mdcUtil = mdcUtil;
    }

    @GetMapping("/sum")
    public ResponseEntity<ApiResponse> sum(@RequestParam("a") BigDecimal a, @RequestParam("b") BigDecimal b) {
        
        log.info("Received sum request: a={}, b={}", a, b);
        
        BigDecimal result = calculatorService.sum(a, b);
        
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Request-ID", mdcUtil.getRequestId());
        
        return ResponseEntity.ok()
            .headers(headers)
            .body(ApiResponse.builder()
                .result(result)
                .build());
    }

    @GetMapping("/subtract")
    public ResponseEntity<ApiResponse> subtract(@RequestParam("a") BigDecimal a, @RequestParam("b") BigDecimal b) {
        
        log.info("Received subtract request: a={}, b={}", a, b);
        
        BigDecimal result = calculatorService.subtract(a, b);
        
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Request-ID", mdcUtil.getRequestId());
        
        return ResponseEntity.ok()
            .headers(headers)
            .body(ApiResponse.builder()
                .result(result)
                .build());
    }

    @GetMapping("/multiply")
    public ResponseEntity<ApiResponse> multiply(@RequestParam("a") BigDecimal a, @RequestParam("b") BigDecimal b) {
        
        log.info("Received multiply request: a={}, b={}", a, b);
        
        BigDecimal result = calculatorService.multiply(a, b);
        
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Request-ID", mdcUtil.getRequestId());
        
        return ResponseEntity.ok()
            .headers(headers)
            .body(ApiResponse.builder()
                .result(result)
                .build());
    }

    @GetMapping("/divide")
    public ResponseEntity<ApiResponse> divide(@RequestParam("a") BigDecimal a, @RequestParam("b") BigDecimal b) {
        
        log.info("Received divide request: a={}, b={}", a, b);
        
        BigDecimal result = calculatorService.divide(a, b);
        
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Request-ID", mdcUtil.getRequestId());

        return ResponseEntity.ok()
            .headers(headers)
            .body(ApiResponse.builder()
                .result(result)
                .build());
    }
}
