package vn.thachnn.service;

import org.springframework.data.domain.Page;
import vn.thachnn.model.Ticket;
import vn.thachnn.model.User;

public interface TicketService {
    Ticket getById(Long id);

    Page<Ticket> getListTicketForUser(User user, int pageNumber, int pageSize);
}
