package vn.thachnn.service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.thachnn.common.PaymentMethod;
import vn.thachnn.model.Payment;
import vn.thachnn.model.Ticket;
import vn.thachnn.repository.PaymentRepository;
import vn.thachnn.service.PaymentService;

import java.time.LocalDateTime;

@Service
@Slf4j(topic = "PAYMENT-SERVICE")
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Payment createNewPayment(String transId, Ticket ticket, Integer amount) {
        log.info("create new payment with transId: {}", transId);

        Payment payment = Payment.builder()
                .amount(amount)
                .paymentTime(LocalDateTime.now())
                .method(PaymentMethod.ZALOPAY)
                .transactionId(transId)
                .ticket(ticket)
                .build();

        return paymentRepository.save(payment);
    }
}
