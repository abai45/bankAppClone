package kz.demo.bankApplication.repository;

import kz.demo.bankApplication.entity.OtpEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OtpRepository extends JpaRepository<OtpEntity,Long> {
    OtpEntity findByEmail(String email);
}
