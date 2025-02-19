package vn.thachnn.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.thachnn.model.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    @Query(value = "SELECT u FROM User u WHERE u.status='ACTIVE' " +
            "AND LOWER(u.username) = :username")
    Optional<User> loadUserByUsername(String username);

    @Query(value = "SELECT u FROM User u WHERE u.status='ACTIVE' " +
            "AND (LOWER(u.fullName) LIKE :keyword " +
            "OR LOWER(u.phone) LIKE :keyword " +
            "OR LOWER(u.email) LIKE :keyword " +
            "OR LOWER(u.username) LIKE :keyword)")
    Page<User> searchByKeyword(String keyword, Pageable pageable);
}
