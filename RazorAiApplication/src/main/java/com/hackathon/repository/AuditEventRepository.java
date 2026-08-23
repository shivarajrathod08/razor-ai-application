package com.hackathon.repository;



import com.hackathon.model.AuditEvent;
import com.hackathon.model.enums.AuditEventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuditEventRepository extends JpaRepository<AuditEvent, Long> {
    Optional<AuditEvent> findByEventId(String eventId);
    List<AuditEvent> findAllByOrderByTimestampDesc();
    List<AuditEvent> findBySessionIdOrderByTimestampDesc(String sessionId);
    List<AuditEvent> findByOrderIdOrderByTimestampDesc(String orderId);
    List<AuditEvent> findByEventTypeOrderByTimestampDesc(AuditEventType eventType);
}