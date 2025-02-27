package vn.thachnn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.thachnn.common.DayType;
import vn.thachnn.common.RoomType;
import vn.thachnn.common.SeatType;
import vn.thachnn.common.ShowtimeType;
import vn.thachnn.model.TicketPrice;

@Repository
public interface TicketPriceRepository extends JpaRepository<TicketPrice, Long>, JpaSpecificationExecutor<TicketPrice> {

    @Query("""
            SELECT COUNT(tp) > 0 FROM TicketPrice tp
            WHERE tp.seatType = :seatType
            AND tp.roomType = :roomType
            AND tp.dayType = :dayType
            AND tp.showtimeType = :showtimeType
            """)
    boolean existsByAttributes(SeatType seatType, RoomType roomType, DayType dayType, ShowtimeType showtimeType);
}
