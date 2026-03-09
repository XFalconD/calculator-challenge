package com.wit.calculator.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.wit.calculator.dto.OperationRequest;
import com.wit.calculator.dto.OperationResponse;
import com.wit.calculator.util.MDCUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CalculatorKafkaListener {



    private final CalculatorService calculatorService;
    private final KafkaTemplate<String, OperationResponse> kafkaTemplate;
    private final MDCUtil mdcUtil;
    private final String responseTopic;

    public CalculatorKafkaListener(CalculatorService calculatorService, KafkaTemplate<String, OperationResponse> kafkaTemplate, MDCUtil mdcUtil,
                                    @Value("${kafka.topic.response}") String responseTopic) {
        this.calculatorService = calculatorService;
        this.kafkaTemplate = kafkaTemplate;
        this.mdcUtil = mdcUtil;
        this.responseTopic = responseTopic;
    }

    @KafkaListener(topics = "${kafka.topic.request}", groupId = "${spring.kafka.consumer.group-id}", containerFactory = "requestKafkaListenerContainerFactory")
    public void listen(OperationRequest request) {
        mdcUtil.setRequestId(request.getRequestId());
        log.info("Received request from Kafka: {}", request);

        OperationResponse response = calculatorService.process(request);
        kafkaTemplate.send(responseTopic, request.getRequestId(), response);
        log.info("Sent response to topic {}: {}", responseTopic, response);
    }
}
