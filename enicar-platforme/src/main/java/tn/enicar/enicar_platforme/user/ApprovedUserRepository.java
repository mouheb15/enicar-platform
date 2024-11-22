package tn.enicar.enicar_platforme.user;


import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ApprovedUserRepository extends JpaRepository<ApprovedUser, Long> {
    Optional<ApprovedUser> findByCin(Integer cin);
}