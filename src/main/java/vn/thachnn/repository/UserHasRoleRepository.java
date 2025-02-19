package vn.thachnn.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import vn.thachnn.model.UserHasRole;

@Repository
public interface UserHasRoleRepository extends CrudRepository<UserHasRole, Long> {
}
