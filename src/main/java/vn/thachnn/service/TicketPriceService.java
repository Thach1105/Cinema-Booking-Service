package vn.thachnn.service;

import org.springframework.data.domain.Page;
import vn.thachnn.common.DayType;
import vn.thachnn.common.RoomType;
import vn.thachnn.common.SeatType;
import vn.thachnn.common.ShowtimeType;
import vn.thachnn.dto.request.TicketPriceRequest;
import vn.thachnn.dto.response.TicketPriceResponse;
import vn.thachnn.model.TicketPrice;

public interface TicketPriceService {

    TicketPriceResponse create(TicketPriceRequest request);

    TicketPriceResponse update(Long ticketPriceId, Integer price);

    TicketPriceResponse getTicketPrice(Long id);

    Page<TicketPrice> getAll(ShowtimeType showtimeType, DayType dayType, RoomType roomType,
                             SeatType seatType, int pageNumber, int pageSize);

    void delete(Long id);
}
