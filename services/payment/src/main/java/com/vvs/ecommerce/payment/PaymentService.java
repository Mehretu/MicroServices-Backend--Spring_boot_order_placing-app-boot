package com.vvs.ecommerce.payment;

import com.vvs.ecommerce.notification.NotificationRequest;
import com.vvs.ecommerce.notification.PaymentProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ModelMapper mapper;
    private final PaymentProducer paymentProducer;
    public Integer createPayment(PaymentRequest request) {
        // create payment object
        var payment = paymentRepository.save(mapper.toPayment(request));
        // send notification to our notification ms
        paymentProducer.sendNotification(
                new NotificationRequest(
                        request.orderReference(),
                        request.amount(),
                        request.paymentMethod(),
                        request.customer().firstname(),
                        request.customer().lastname(),
                        request.customer().email()
                )
        );


        return payment.getId();
    }
}
