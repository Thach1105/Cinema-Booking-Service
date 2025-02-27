package vn.thachnn.init;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.thachnn.common.UserStatus;
import vn.thachnn.common.UserType;
import vn.thachnn.model.Role;
import vn.thachnn.model.User;
import vn.thachnn.model.UserHasRole;
import vn.thachnn.repository.RoleRepository;
import vn.thachnn.repository.UserHasRoleRepository;
import vn.thachnn.repository.UserRepository;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j(topic = "INIT-SERVICE")
public class InitService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserHasRoleRepository userHasRoleRepository;

    @Transactional
    public void initializeRole(){
        List<Role> defaultRoles = Arrays.asList(
                Role.builder().name("ADMIN").description("Administrator role").build(),
                Role.builder().name("MANAGER").description("Management role").build(),
                Role.builder().name("STAFF").description("Staff role").build(),
                Role.builder().name("USER").description("User role").build()
        );

        for (Role role : defaultRoles){
            if(!roleRepository.existsByName(role.getName())){
                roleRepository.save(role);
                log.info("Create {} successfully", role.getDescription());
            }
        }
    }

    @Transactional
    public void initializeUser(){
        Role adminRole = roleRepository.findByName("ADMIN").orElseThrow();

        User adminAccount = User.builder()
                .fullName("administrator root")
                .email("thach11052002@gmail.com")
                .dateOfBirth(LocalDate.now())
                .username("ADMIN")
                .password(passwordEncoder.encode("admin11052002"))
                .status(UserStatus.ACTIVE)
                .type(UserType.ADMIN)
                .build();

        if(!userRepository.existsByUsername(adminAccount.getUsername())){
            userRepository.save(adminAccount);

            UserHasRole userHasRole = UserHasRole.builder()
                    .role(adminRole)
                    .user(adminAccount)
                    .build();
            userHasRoleRepository.save(userHasRole);

            log.info("Create admin account successfully with username: ADMIN");
        }
    }
}
