package vn.thachnn.repository.specification;

import org.springframework.data.jpa.domain.Specification;
import vn.thachnn.common.DayType;
import vn.thachnn.common.RoomType;
import vn.thachnn.common.SeatType;
import vn.thachnn.common.ShowtimeType;
import vn.thachnn.model.TicketPrice;

public class TicketPriceSpecification {

    public static Specification<TicketPrice> hasShowtimeType(ShowtimeType showtimeType) {
        return (root, query, criteriaBuilder) ->
                showtimeType == null ? null : criteriaBuilder.equal(root.get("showtimeType"), showtimeType);
    }

    public static Specification<TicketPrice> hasDayType(DayType dayType){
        return (root, query, criteriaBuilder) ->
                dayType == null ? null : criteriaBuilder.equal(root.get("dayType"), dayType);
    }

    public static Specification<TicketPrice> hasRoomType(RoomType roomType){
        return (root, query, criteriaBuilder) ->
                roomType == null ? null : criteriaBuilder.equal(root.get("roomType"), roomType);
    }

    public static Specification<TicketPrice> hasSeatType(SeatType seatType){
        return (root, query, criteriaBuilder) ->
                seatType == null ? null : criteriaBuilder.equal(root.get("seatType"), seatType);
    }
}
