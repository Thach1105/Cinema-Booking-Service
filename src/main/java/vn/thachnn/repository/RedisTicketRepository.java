package vn.thachnn.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import vn.thachnn.model.RedisTicket;

@Repository
public interface RedisTicketRepository extends CrudRepository<RedisTicket, String> {
}
