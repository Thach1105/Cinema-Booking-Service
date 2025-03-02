package vn.thachnn.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vn.thachnn.dto.request.UserCreationRequest;
import vn.thachnn.dto.request.UserPasswordRequest;
import vn.thachnn.dto.request.UserUpdateRequest;
import vn.thachnn.dto.response.ApiResponse;
import vn.thachnn.dto.response.PaginationResponse;
import vn.thachnn.dto.response.UserResponse;
import vn.thachnn.mapper.UserMapper;
import vn.thachnn.model.User;
import vn.thachnn.service.Impl.UserServiceImpl;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
@Tag(name = "User Controller")
@Slf4j(topic = "USER-CONTROLLER")
public class UserController {

    private final UserServiceImpl userService;
    private final UserMapper userMapper;

    @Operation(summary = "Create user", description = "API add new user to database for user")
    @PostMapping("/register")
    public ResponseEntity<?> createUser(@RequestBody @Valid UserCreationRequest request) {
        log.info("Create new user: {}", request);

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .status(HttpStatus.CREATED.value())
                .message("Created user successfully")
                .data(userService.create(request))
                .build();

        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    @Operation(summary = "Get user by Id", description = "API retrieve user detail by ID from database")
    @GetMapping("/{userId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> getUserById(
            @PathVariable @Min(value = 1, message = "userId must be equal or greater than 1") Long userId){
        log.info("Find user with Id: {}", userId);

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("user")
                .data(userService.findById(userId))
                .build();

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @Operation(summary = "Get user list", description = "API retrieve users from database")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> getList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "20") int pageSize
    ){
        log.info("Get user list");
        Page<User> userPage = userService.findAll(keyword, sort, pageNumber, pageSize);
        List<UserResponse> responseList = userPage.getContent().stream().map(userMapper::toUserResponse).toList();

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("users list")
                .data(responseList)
                .pagination(PaginationResponse.builder()
                        .pageNumber(userPage.getNumber()+1)
                        .pageSize(userPage.getSize())
                        .totalPages(userPage.getTotalPages())
                        .totalElements(userPage.getTotalElements())
                        .build())
                .build();

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/my-profile")
    @Operation(
            summary = "Get current user profile",
            description = "Retrieves the profile details of the currently authenticated user."
    )
    public ResponseEntity<?> getMyProfile(
            @AuthenticationPrincipal User user
    ){
        log.info("Get user {} profile", user.getId());

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("my-profile")
                .data(userMapper.toUserResponse(user))
                .build();

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @Operation(summary = "Update user", description = "API update user to database")
    @PutMapping("/{userId}")
    public ResponseEntity<?> updateUser(
            @PathVariable @Min(value = 1, message = "userId must be equal or greater than 1") Long userId,
            @RequestBody @Valid UserUpdateRequest request
    ){
        log.info("Update user with Id: {}", userId);

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("user")
                .data(userService.update(userId, request))
                .build();

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @Operation(summary = "Delete user", description = "API delete user")
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUser(
            @PathVariable @Min(value = 1, message = "userId must be equal or greater than 1") Long userId
    ){
        log.info("Delete user with Id: {}", userId);
        userService.delete(userId);

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .status(HttpStatus.RESET_CONTENT.value())
                .message("User deleted successfully")
                .data("")
                .build();

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @Operation(summary = "Change Password", description = "API change password for user to database")
    @PatchMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @RequestBody @Valid UserPasswordRequest request,
            @AuthenticationPrincipal User user
    ){
        log.info("Change password user: {}", request);
        userService.changePassword(request, user);

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Changed password successfully")
                .data("")
                .build();

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}
