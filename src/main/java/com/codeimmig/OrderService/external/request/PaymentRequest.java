package com.codeimmig.OrderService.external.request;

import com.codeimmig.OrderService.model.PaymentMode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentRequest {
    private long orderId;
    private long amount;
    private String reference;
    private PaymentMode paymentMode;
}
