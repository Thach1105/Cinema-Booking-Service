package vn.thachnn.mapper;

import org.mapstruct.Mapper;
import vn.thachnn.dto.request.TicketPriceRequest;
import vn.thachnn.dto.response.TicketPriceResponse;
import vn.thachnn.model.TicketPrice;

@Mapper(componentModel = "spring")
public interface TicketPriceMapper {

    TicketPrice toTicketPrice(TicketPriceRequest request);

    TicketPriceResponse toTicketPriceResponse(TicketPrice ticketPrice);
}
