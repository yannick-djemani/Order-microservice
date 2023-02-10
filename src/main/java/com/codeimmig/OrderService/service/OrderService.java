package com.codeimmig.OrderService.service;

import com.codeimmig.OrderService.model.OrderResponse;
import com.codeimmig.OrderService.model.OrderRequest;

public interface OrderService {
    long placeOrder(OrderRequest orderRequest);

    OrderResponse getOrdersDetails(long orderId);
}
