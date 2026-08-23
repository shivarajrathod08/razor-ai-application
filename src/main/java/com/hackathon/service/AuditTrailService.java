package com.hackathon.service;



import com.fasterxml.jackson.databind.ObjectMapper;
import com.hackathon.dto.AuditEventDto;
import com.hackathon.dto.SafetyCheckDetailDto;
import com.hackathon.model.AuditEvent;
import com.hackathon.model.enums.ActorType;
import com.hackathon.model.enums.AuditEventType;
import com.hackathon.repository.AuditEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuditTrailService {
    private static final Logger log = LoggerFactory.getLogger(AuditTrailService.class);
    private final AuditEventRepository auditEventRepository;
    private final ObjectMapper objectMapper;

    public AuditTrailService(AuditEventRepository auditEventRepository, ObjectMapper objectMapper) {
        this.auditEventRepository = auditEventRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public AuditEvent logEvent(String sessionId, String orderId, String paymentId,
                               AuditEventType eventType, ActorType actor,
                               String description, BigDecimal amount, boolean success, Object metadata) {
        String metaJson = null;
        if (metadata != null) {
            try {
                if (metadata instanceof String) {
                    metaJson = (String) metadata;
                } else {
                    metaJson = objectMapper.writeValueAsString(metadata);
                }
            } catch (Exception e) {
                metaJson = "{\"meta_error\": \"" + e.getMessage() + "\"}";
            }
        }

        AuditEvent event = new AuditEvent(sessionId, orderId, eventType, actor, description, amount, success, metaJson);
        event.setPaymentId(paymentId);
        AuditEvent saved = auditEventRepository.save(event);
        log.info("[AUDIT] [{}] [{}] Actor: {} | Order: {} | Session: {} | Amount: {} | Success: {} | Desc: {}",
                saved.getEventId(), eventType, actor, orderId, sessionId, amount, success, description);
        return saved;
    }

    @Transactional
    public AuditEvent logSafetyCheck(String sessionId, String orderId, SafetyCheckDetailDto safetyDetails, boolean approved) {
        String desc = approved ?
                "Payment Safety Guard APPROVED checkout: All 8 deterministic integrity checks verified." :
                "Payment Safety Guard BLOCKED checkout: Violated safety constraints (" + String.join(", ", safetyDetails.getReasons()) + ")";

        return logEvent(sessionId, orderId, null,
                AuditEventType.PAYMENT_SAFETY_CHECK,
                ActorType.BACKEND_SAFETY_GUARD,
                desc,
                safetyDetails.getCalculatedAmount(),
                approved,
                safetyDetails);
    }

    @Transactional(readOnly = true)
    public List<AuditEventDto> getAllEvents() {
        return auditEventRepository.findAllByOrderByTimestampDesc()
                .stream().map(AuditEventDto::fromEntity).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AuditEventDto> getEventsBySession(String sessionId) {
        return auditEventRepository.findBySessionIdOrderByTimestampDesc(sessionId)
                .stream().map(AuditEventDto::fromEntity).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AuditEventDto> getEventsByOrder(String orderId) {
        return auditEventRepository.findByOrderIdOrderByTimestampDesc(orderId)
                .stream().map(AuditEventDto::fromEntity).collect(Collectors.toList());
    }
}