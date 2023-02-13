package com.codeimmig.OrderService.service;

import com.codeimmig.OrderService.entity.Order;
import com.codeimmig.OrderService.exception.CustomException;
import com.codeimmig.OrderService.external.client.PaymentService;
import com.codeimmig.OrderService.external.client.ProductService;
import com.codeimmig.OrderService.external.request.PaymentRequest;
import com.codeimmig.OrderService.external.response.PaymentResponse;
import com.codeimmig.OrderService.model.OrderResponse;
import com.codeimmig.OrderService.model.OrderRequest;
import com.codeimmig.OrderService.repository.OrderRepository;
import com.codeimmig.ProductService.model.ProductResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ProductService productService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public long placeOrder(OrderRequest orderRequest) {
        //Order Entity -> Save the data with Status Order Created
        //Product Service - Block Products(reduce the Quantity)
        //Payment Service -> Payments -> Success -> COMPLETE, Else
        //CANCELLED
        log.info("Placing Order Request: {}", orderRequest);
        productService.reduceQuantity(orderRequest.getProductId(), orderRequest.getQuantity());
        log.info("Creating Order with status created");

        Order order = Order.builder()
                .amount(orderRequest.getTotalAmount())
                .orderStatus("CTREATED")
                .productId(orderRequest.getProductId())
                .orderDate(Instant.now())
                .quantity(orderRequest.getQuantity())
                .build();

        order = orderRepository.save(order);
        log.info("Calling payment service to complete the payment");
        PaymentRequest paymentRequest = PaymentRequest.builder()
                .orderId(order.getId())
                .paymentMode(orderRequest.getPaymentMode())
                .amount(orderRequest.getTotalAmount())
                .build();
        String orderStatus=null;
        try {
            paymentService.doPayment(paymentRequest);
            log.info("Payment done successfully.Changing the order status to PLACED");
            orderStatus="PLACED";

        }catch (Exception e ){
            log.error("Error occur in payment.Changing the order status to FAILED");
            orderStatus="PAYMENT_FAILED";

        }
        order.setOrderStatus(orderStatus);
        orderRepository.save(order);

        log.info("Order place successfull with orderId :{}", order.getId());
        log.info("Ending the task execution of oder service");
        return order.getId();

    }

    @Override
    public OrderResponse getOrdersDetails(long orderId) {
        log.info("Get Order details for order id: {} ", orderId);
        Order order=orderRepository.findById(orderId).orElseThrow(()->new CustomException("Order not found for the oderId : "+orderId+" ", "NOT_FOUND", 404));
        log.info("Invoking product Response to fetch the product for id : {}",order.getProductId());
        ProductResponse productResponse=restTemplate.getForObject("http://PRODUCT-SERVICE/product/"+order.getProductId(), ProductResponse.class);
        log.info("Getting payment information from the payment service ");
        PaymentResponse paymentResponse=restTemplate.getForObject("http://PAYMENT-SERVICE/payment/order/"+order.getId(), PaymentResponse.class );
        assert productResponse != null;
        OrderResponse.ProdutDetails produtDetails=OrderResponse.ProdutDetails
                .builder()
                .productName(productResponse.getProductName())
                .quantity(productResponse.getQuantity())
                .price(productResponse.getPrice())
                .productId(productResponse.getProductId())
                .build();
        assert paymentResponse != null;
        OrderResponse.PaymentDetails paymentDetails=OrderResponse.PaymentDetails
                .builder()
                .paymentId(paymentResponse.getPaymentId())
                .paymentStatus(paymentResponse.getStatus())
                .paymentMode(paymentResponse.getPaymentMode())
                .paymentDate(paymentResponse.getPaymentyDate())
                .build();
        OrderResponse orderResponse=OrderResponse
                .builder()
                .orderId(order.getId())
                .orderStatus(order.getOrderStatus())
                .amount(order.getAmount())
                .orderDate(order.getOrderDate())
                .produtDetails(produtDetails)
                .paymentDetails(paymentDetails)
                .build();
        return orderResponse;
    }
}
