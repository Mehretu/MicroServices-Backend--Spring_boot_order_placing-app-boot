package com.vvs.ecommerce.kafka;

import com.vvs.ecommerce.customer.CustomerResponse;
import com.vvs.ecommerce.order.PaymentMethod;
import com.vvs.ecommerce.product.PurchaseResponse;

import java.math.BigDecimal;
import java.util.List;

public record OrderConfirmation(
        String orderReference,
        BigDecimal totalAmount,
        PaymentMethod paymentMethod,
        CustomerResponse customer,
        List<PurchaseResponse> products

) {
}
