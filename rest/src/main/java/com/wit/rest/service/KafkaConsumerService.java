package com.wit.rest.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.wit.rest.dto.OperationResponse;
import com.wit.rest.util.MDCUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class KafkaConsumerService {

    private final Map<String, OperationResponse> responseCache = new ConcurrentHashMap<>();
    private final MDCUtil mdcUtil;

    public KafkaConsumerService(MDCUtil mdcUtil) {
        this.mdcUtil = mdcUtil;
    }

    @KafkaListener(topics = "${kafka.topic.response}", groupId = "${spring.kafka.consumer.group-id}")
    public void handleOperationResponse(OperationResponse response) {
        try {
            mdcUtil.setRequestId(response.getRequestId());
            log.info("Received operation response: requestId={}, status={}, result={}",
                response.getRequestId(), response.getStatus(), response.getResult());
            
            responseCache.put(response.getRequestId(), response);
        } catch (Exception e) {
            log.error("Error processing operation response", e);
        }
    }

    public OperationResponse getResponse(String requestId) {
        return responseCache.get(requestId);
    }

    public void removeResponse(String requestId) {
        responseCache.remove(requestId);
    }

    public boolean hasResponse(String requestId) {
        return responseCache.containsKey(requestId);
    }
}
