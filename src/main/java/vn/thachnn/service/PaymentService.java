package vn.thachnn.service;

import vn.thachnn.model.Payment;
import vn.thachnn.model.Ticket;

public interface PaymentService {

    Payment createNewPayment(String transId, Ticket ticket, Integer amount);
}
