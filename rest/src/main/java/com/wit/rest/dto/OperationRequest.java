package com.wit.rest.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OperationRequest {
    private String requestId;
    private OperationType operation;
    private BigDecimal operandA;
    private BigDecimal operandB;
}
