package net.javaguides.ems.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "failed_integration_request")
@Getter
@Setter
@NoArgsConstructor
public class FailedIntegrationRequest {

  public static final String TYPE_DOWNSTREAM = "DOWNSTREAM";
  public static final String STATUS_PENDING = "PENDING";
  public static final String STATUS_RECOVERED = "RECOVERED";
  public static final String STATUS_ABANDONED = "ABANDONED";

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 32)
  private String integrationType;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String payloadJson;

  @Column(nullable = false, length = 16)
  private String status;

  private int attemptCount;

  @Column(columnDefinition = "TEXT")
  private String lastError;

  private LocalDateTime createdAt;

  private LocalDateTime recoveredAt;
}
