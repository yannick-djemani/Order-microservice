package com.codeimmig.OrderService.external.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ErrorResponse {
    private  String errorMessage;
    private  String errorCode;
}
