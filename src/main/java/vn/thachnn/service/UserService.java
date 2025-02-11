package vn.thachnn.service;

import org.springframework.data.domain.Page;
import vn.thachnn.dto.request.UserCreationRequest;
import vn.thachnn.dto.request.UserPasswordRequest;
import vn.thachnn.dto.request.UserUpdateRequest;
import vn.thachnn.dto.response.UserResponse;
import vn.thachnn.model.User;

public interface UserService {

    UserResponse create(UserCreationRequest request);

    UserResponse findById(Long userId);

    UserResponse update(Long userId, UserUpdateRequest request);

    Page<User> findAll(String keyword, String sort, int page, int size);

    void delete(Long userId);

    void changePassword(UserPasswordRequest request);
}
