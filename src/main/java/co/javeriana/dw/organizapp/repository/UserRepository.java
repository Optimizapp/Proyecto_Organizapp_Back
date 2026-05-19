package co.javeriana.dw.organizapp.repository;

import co.javeriana.dw.organizapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    boolean existsByRolId(Long roleId);

    Optional<User> findByEmail(String email);
}
