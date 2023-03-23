package com.codeimmig.OrderService.service;

import com.codeimmig.OrderService.entity.Order;
import com.codeimmig.OrderService.exception.CustomException;
import com.codeimmig.OrderService.external.client.PaymentService;
import com.codeimmig.OrderService.external.client.ProductService;
import com.codeimmig.OrderService.external.request.PaymentRequest;
import com.codeimmig.OrderService.external.response.PaymentResponse;
import com.codeimmig.OrderService.model.OrderRequest;
import com.codeimmig.OrderService.model.OrderResponse;
import com.codeimmig.OrderService.model.PaymentMode;
import com.codeimmig.OrderService.repository.OrderRepository;
import com.codeimmig.ProductService.model.ProductResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class OrderServiceImplTest {
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private ProductService productService;

    @Mock
    private PaymentService paymentService;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    OrderService orderService=new OrderServiceImpl();

    @Test
    @DisplayName("Get Order - success Scenario")
    void test_When_Order_Success(){
        Order order=getMockOder();
        //Mocking
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));
        when(restTemplate.getForObject("http://PRODUCT-SERVICE/product/"+order.getProductId(), ProductResponse.class)).thenReturn(getMockProductResponse());
        when(restTemplate.getForObject("http://PAYMENT-SERVICE/payment/order/"+order.getId(), PaymentResponse.class )).thenReturn(getMockPaymentResponse());
        //Actual
        OrderResponse orderResponse = orderService.getOrdersDetails(7);
        //verification
        verify(orderRepository, times(1)).findById(anyLong());
        verify(restTemplate,times(1) ).getForObject("http://PRODUCT-SERVICE/product/"+order.getProductId(), ProductResponse.class);
        verify(restTemplate,times(1) ).getForObject("http://PAYMENT-SERVICE/payment/order/"+order.getId(), PaymentResponse.class );

        //Assert
        assertNotNull(orderResponse);
        assertEquals(order.getId(), orderResponse.getOrderId());

    }

    @DisplayName("Get Orders-Failure Scenario")
    @Test
    void test_When_Order_Not_FOUND_Then_Not_Found(){
        when(orderRepository.findById(anyLong())).thenReturn(Optional.ofNullable(null));
        CustomException exception=assertThrows(CustomException.class, ()-> orderService.getOrdersDetails(7));
        assertEquals("NOT_FOUND", exception.getErrorCode());
        assertEquals(404, exception.getStatus());
        verify(orderRepository, times(1)).findById(anyLong());

    }
    @DisplayName("Place Order -Success Scenario")
    @Test
    void test_When_Place_Order_Success(){
        Order order=getMockOder();
        OrderRequest orderRequest=getMockOderRequest();
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(productService.reduceQuantity(anyLong(), anyLong())).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        when(paymentService.doPayment(any(PaymentRequest.class))).thenReturn(new ResponseEntity<>(1L,HttpStatus.OK));

        long orderId=orderService.placeOrder(orderRequest);
        verify(orderRepository, times(2)).save(any());
        verify(productService, times(1)).reduceQuantity(anyLong(), anyLong());
        verify(paymentService, times(1)).doPayment(any(PaymentRequest.class));
        assertEquals(order.getId(),orderId );

    }

    @DisplayName("Place Payment -Fails Scenario")
    @Test
    void test_When_Place_Order_Payment_Fail_Then_Order_Place(){
        Order order=getMockOder();
        OrderRequest orderRequest=getMockOderRequest();
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(productService.reduceQuantity(anyLong(), anyLong())).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        when(paymentService.doPayment(any(PaymentRequest.class))).thenThrow(new RuntimeException());

        long orderId=orderService.placeOrder(orderRequest);
        verify(orderRepository, times(2)).save(any());
        verify(productService, times(1)).reduceQuantity(anyLong(), anyLong());
        verify(paymentService, times(1)).doPayment(any(PaymentRequest.class));
        assertEquals(order.getId(),orderId );

    }

    private OrderRequest getMockOderRequest() {
        return OrderRequest
                 .builder()
                .productId(7)
                .quantity(10)
                .paymentMode(PaymentMode.CASH)
                .totalAmount(100)
                .build();
    }

    private PaymentResponse getMockPaymentResponse() {
       return  PaymentResponse
                .builder()
                .paymentId(1)
                .paymentyDate(Instant.now())
                .paymentMode(PaymentMode.CASH)
               .amount(200)
               .oderId(5)
                .build();
    }

    private ProductResponse getMockProductResponse() {
        return ProductResponse
                .builder()
                .productName("Phone")
                .price(100)
                .productId(1)
                .quantity(200)
                .build();
    }

    private Order getMockOder() {
        return Order.builder()
                .orderStatus("PLACED")
                .orderDate(Instant.now())
                .id(5)
                .quantity(200)
                .amount(100)
                .productId(2)
                .build();
    }

}