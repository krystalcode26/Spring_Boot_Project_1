package net.javaguides.ems.repository;

import net.javaguides.ems.entity.FailedIntegrationRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FailedIntegrationRequestRepository extends JpaRepository<FailedIntegrationRequest, Long> {

  List<FailedIntegrationRequest> findByStatusAndIntegrationType(String status, String integrationType);
}
