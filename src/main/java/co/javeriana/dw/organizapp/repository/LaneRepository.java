package co.javeriana.dw.organizapp.repository;

import co.javeriana.dw.organizapp.entity.Lane;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LaneRepository extends JpaRepository<Lane, Long> {
    List<Lane> findByPoolId(Long poolId);

    boolean existsByPoolId(Long poolId);

    boolean existsByPoolIdAndName(Long poolId, String name);
}
