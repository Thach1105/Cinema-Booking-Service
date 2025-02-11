package vn.thachnn.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import vn.thachnn.model.EmailCode;

@Repository
public interface EmailCodeRepository extends CrudRepository<EmailCode, String> {
}
