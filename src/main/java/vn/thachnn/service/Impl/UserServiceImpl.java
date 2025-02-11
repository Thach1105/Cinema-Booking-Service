package vn.thachnn.service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import vn.thachnn.common.Gender;
import vn.thachnn.common.UserStatus;
import vn.thachnn.common.UserType;
import vn.thachnn.dto.request.UserCreationRequest;
import vn.thachnn.dto.request.UserPasswordRequest;
import vn.thachnn.dto.request.UserUpdateRequest;
import vn.thachnn.dto.response.UserResponse;
import vn.thachnn.exception.InvalidDataException;
import vn.thachnn.exception.ResourceNotFoundException;
import vn.thachnn.mapper.UserMapper;
import vn.thachnn.model.User;
import vn.thachnn.repository.UserRepository;
import vn.thachnn.service.EmailService;
import vn.thachnn.service.UserService;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "USER-SERVICE")
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserResponse create(UserCreationRequest request) {
       log.info("Creating user: {}", request);
       User user = userMapper.toUserByCreateRequest(request);
       log.info("New User: {}", user);
       user.setPassword(passwordEncoder.encode(request.getPassword()));
       user.setStatus(UserStatus.NONE);
       user.setType(UserType.USER);

       User newUser = userRepository.save(user);
       log.info("Saved user: {}", user);

       try {
           emailService.sendVerificationEmail(newUser.getEmail(), newUser.getUsername(), newUser.getFullName());
       } catch (Exception e) {
           log.error("Verification sent failed, errorMessage= {}", e.getMessage());
           throw new RuntimeException(e);
       }

        return userMapper.toUserResponse(newUser);
    }

    @Override
    public UserResponse findById(Long userId) {
        log.info("Find user by ID: {}", userId);
        User user = getUser(userId);
        return userMapper.toUserResponse(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserResponse update(Long userId, UserUpdateRequest request) {
        log.info("Updating user with ID: {}", request.getId());
        User user = getUser(userId);
        user.setFullName(request.getFullName());
        user.setGender(Gender.valueOf(request.getGender()));
        user.setDateOfBirth(request.getDateOfBirth());
        user.setPhone(request.getPhone());

        userRepository.save(user);
        return userMapper.toUserResponse(user);
    }

    @Override
    public Page<User> findAll(String keyword, String sort, int page, int size) {
        log.info("FindAll start");

        //Sorting
        Sort.Order order = new Sort.Order(Sort.Direction.ASC, "id");
        if(StringUtils.hasLength(sort)){
            // tencot_asc|desc
            String[] arrSort = new String[2];
            arrSort = sort.split("_");
            if(StringUtils.hasLength(arrSort[0])) {
                if(arrSort[1].equals("asc")){
                    order = new Sort.Order(Sort.Direction.ASC, arrSort[0]);
                } else {
                    order = new Sort.Order(Sort.Direction.DESC, arrSort[0]);
                }
            }
        }

        //Paging
        Pageable pageable = PageRequest.of(page-1, size, Sort.by(order));
        Page<User> userPage;
        if(StringUtils.hasLength(keyword)){
            userPage = userRepository.searchByKeyword(keyword, pageable);
        } else {
            userPage = userRepository.findAll(pageable);
        }

        return userPage;
    }


    @Override
    public void delete(Long userId) {
        log.info("Deleting user: {}", userId);

        // Get user by id
        User user = getUser(userId);
        user.setStatus(UserStatus.INACTIVE);
        userRepository.save(user);
        log.info("Deleted user: {}", userId);
    }

    @Override
    public void changePassword(UserPasswordRequest request) {
        log.info("Changing password for user: {}", request);

        User user = getUser(request.getId());
        if(request.getPassword().equals(request.getConfirmPassword())){
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        } else throw new InvalidDataException("Confirmation password is incorrect");

        userRepository.save(user);
        log.info("Changed password for user: {}", user);
    }

    private User getUser(Long userId){
        return userRepository.findById(userId).orElseThrow(
                () -> new ResourceNotFoundException("User not found")
        );
    }

    /*private User getUser(String username){
        return userRepository.findByUsername(username).orElseThrow(
                () -> new ResourceNotFoundException("User not found")
        );
    }*/
}
