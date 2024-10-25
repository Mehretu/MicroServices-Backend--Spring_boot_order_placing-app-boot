package com.vvs.ecommerce.order;

import com.vvs.ecommerce.customer.CustomerClient;
import com.vvs.ecommerce.exception.BusinessException;
import com.vvs.ecommerce.kafka.OrderConfirmation;
import com.vvs.ecommerce.kafka.OrderProducer;
import com.vvs.ecommerce.orderline.OrderLineRequest;
import com.vvs.ecommerce.orderline.OrderLineService;
import com.vvs.ecommerce.payment.PaymentClient;
import com.vvs.ecommerce.payment.PaymentRequest;
import com.vvs.ecommerce.product.ProductClient;
import com.vvs.ecommerce.product.PurchaseRequest;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final CustomerClient customerClient;
    private final ProductClient productClient;
    private final OrderRepository orderRepository;
    private final OrderLineService orderLineService;
    private final OrderMapper mapper;
    private final OrderProducer orderProducer;
    private final PaymentClient paymentClient;
    public Integer createOrder(@Valid OrderRequest request) {
        //check the customer ----> use OpenFeign
        var customer = this.customerClient.findCustomerById(request.customerId())
                .orElseThrow(() -> new BusinessException("Cannot create order:: No Customer found with the provided customer id"));

        //purchase the product ---> using the product microservice(Using Rest Template)
        var products = this.productClient.purchaseProducts(request.products());
        //persist order
        var order = this.orderRepository.save(mapper.toOrder(request));

        //persist order-lines
        for (PurchaseRequest purchaseRequest : request.products()){
            orderLineService.saveOrderLine(
                    new OrderLineRequest(
                            null,
                            order.getId(),
                            purchaseRequest.productId(),
                            purchaseRequest.quantity()
                    )
            );

        }

        // todo start payment process
        var paymentRequest = new PaymentRequest(
                request.amount(),
                request.paymentMethod(),
                order.getId(),
                order.getReference(),
                customer
        );
        paymentClient.requestOrderPayment(paymentRequest);


        // send the order confirmation to --> notification microservice(kafka)
        orderProducer.sendOrderConfirmation(
                new OrderConfirmation(
                        request.reference(),
                        request.amount(),
                        request.paymentMethod(),
                        customer,
                        products
                )
        );
        return order.getId();
    }

    public List<OrderResponse> findAll() {
        return orderRepository.findAll()
                .stream()
                .map(mapper::fromOrder)
                .collect(Collectors.toList());
    }

    public OrderResponse findById(Integer orderId) {
        return orderRepository.findById(orderId)
                .map(mapper::fromOrder)
                .orElseThrow(() ->
                        new EntityNotFoundException(String.format("No Order found with the provided id %s", orderId)));
    }
}
