package com.wit.rest.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.wit.rest.dto.OperationRequest;
import com.wit.rest.util.MDCUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class KafkaProducerService {

    private final KafkaTemplate<String, OperationRequest> kafkaTemplate;
    private final MDCUtil mdcUtil;
    private final String requestTopic;

    public KafkaProducerService(KafkaTemplate<String, OperationRequest> kafkaTemplate, MDCUtil mdcUtil, @Value("${kafka.topic.request}") String requestTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.mdcUtil = mdcUtil;
        this.requestTopic = requestTopic;
    }

    public void sendOperationRequest(OperationRequest request) {
        try {
            log.info("Sending operation request: operation={}, operandA={}, operandB={}",
                request.getOperation(), request.getOperandA(), request.getOperandB());
            
            kafkaTemplate.send(requestTopic, request.getRequestId(), request)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to send operation request with requestId={}", request.getRequestId(), ex);
                    } else {
                        log.info("Operation request sent successfully with requestId={}", request.getRequestId());
                    }
                });
        } catch (Exception e) {
            log.error("Error sending operation request with requestId={}", request.getRequestId(), e);
            throw e;
        }
    }
}
