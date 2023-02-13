package com.codeimmig.OrderService.controller;

import com.codeimmig.OrderService.model.OrderRequest;
import com.codeimmig.OrderService.model.OrderResponse;
import com.codeimmig.OrderService.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    //@PreAuthorize("hasAnyAuthority('Customer')")
    @PostMapping("/placeOrder")
    public ResponseEntity<Long> placeOder(@RequestBody OrderRequest orderRequest){
        long orderId=orderService.placeOrder(orderRequest);
        log.info("Order Id:{}", orderId);
        return new  ResponseEntity<>(orderId, HttpStatus.OK);
    }

//@PreAuthorize("hasAnyAuthority('Admin') || hasAnyAuthority('Customer')")
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrdersDetails(@PathVariable long orderId){
        OrderResponse orderResponse=orderService.getOrdersDetails(orderId);
        return  new ResponseEntity<>(orderResponse, HttpStatus.OK);
    }
}
