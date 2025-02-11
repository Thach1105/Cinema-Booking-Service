package vn.thachnn.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vn.thachnn.dto.request.UserCreationRequest;
import vn.thachnn.dto.response.UserResponse;
import vn.thachnn.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toUserByCreateRequest (UserCreationRequest request);

    UserResponse toUserResponse (User user);
}
