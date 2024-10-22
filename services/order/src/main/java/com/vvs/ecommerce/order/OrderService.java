package com.vvs.ecommerce.order;

import com.vvs.ecommerce.customer.CustomerClient;
import com.vvs.ecommerce.exception.BusinessException;
import com.vvs.ecommerce.orderline.OrderLineRequest;
import com.vvs.ecommerce.orderline.OrderLineService;
import com.vvs.ecommerce.product.ProductClient;
import com.vvs.ecommerce.product.PurchaseRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final CustomerClient customerClient;
    private final ProductClient productClient;
    private final OrderRepository orderRepository;
    private final OrderLineService orderLineService;
    private final OrderMapper mapper;
    public Integer createOrder(@Valid OrderRequest request) {
        //check the customer ----> use OpenFeign
        var customer = this.customerClient.findCustomerById(request.customerId())
                .orElseThrow(() -> new BusinessException("Cannot create order:: No Customer found with the provided customer id"));

        //purchase the product ---> using the product microservice(Using Rest Template)
        this.productClient.purchaseProducts(request.products());
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


        // send the order confirmation to --> notification microservice(kafka)
        return null;
    }
}
