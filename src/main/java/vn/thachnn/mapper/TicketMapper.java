package vn.thachnn.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vn.thachnn.dto.response.TicketResponse;
import vn.thachnn.model.Ticket;
import vn.thachnn.model.TicketDetail;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface TicketMapper {

    @Mapping(target = "details", expression = "java(mapDetails(ticket))")
    TicketResponse toTicketResponse(Ticket ticket);

    default List<JsonNode> mapDetails(Ticket ticket) {
        List<JsonNode> detailResponse = new ArrayList<>();
        List<TicketDetail> details = ticket.getDetails().stream().toList();

        ObjectMapper objectMapper = new ObjectMapper();
        for (var d : details){
            ObjectNode detailJson = objectMapper.createObjectNode();
            detailJson.put("seatNumber", d.getSeatNumber());
            detailJson.put("seatType", d.getSeatType());
            detailJson.put("price", d.getPrice());

            detailResponse.add(detailJson);
        }

        return detailResponse;
    }
}
