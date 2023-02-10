package com.codeimmig.OrderService.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private long orderId;
    private Instant orderDate;
    private String orderStatus;
    private long amount;
    private  ProdutDetails produtDetails;
    private  PaymentDetails paymentDetails;


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProdutDetails {
        private String productName;
        private long productId;
        private long price;
        private long quantity;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class PaymentDetails {
        private long paymentId;
        private  PaymentMode paymentMode;
        private  String paymentStatus;
        private  Instant paymentDate;

    }
}
