package com.vvs.ecommerce.notification;

import com.vvs.ecommerce.payment.PaymentMethod;

import java.math.BigDecimal;

public record NotificationRequest(
        String orderReference,
        BigDecimal amount,
        PaymentMethod paymentMethod,
        String customerFirstname,
        String customerLastname,
        String customerEmail
) {
}
