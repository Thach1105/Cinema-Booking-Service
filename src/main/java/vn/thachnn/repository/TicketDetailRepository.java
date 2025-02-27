package vn.thachnn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.thachnn.model.TicketDetail;

@Repository
public interface TicketDetailRepository extends JpaRepository<TicketDetail, Long> {
}
