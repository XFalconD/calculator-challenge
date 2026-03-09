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
public class OperationResponse {
    private String requestId;
    private BigDecimal result;
    private String status;
    private String errorMessage;
}
