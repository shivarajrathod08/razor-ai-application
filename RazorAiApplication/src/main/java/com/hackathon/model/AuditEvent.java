package com.hackathon.model;



import com.hackathon.model.enums.ActorType;
import com.hackathon.model.enums.AuditEventType;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "audit_events", indexes = {
        @Index(name = "idx_audit_session", columnList = "sessionId"),
        @Index(name = "idx_audit_order", columnList = "orderId"),
        @Index(name = "idx_audit_event_type", columnList = "eventType"),
        @Index(name = "idx_audit_timestamp", columnList = "timestamp")
})
public class AuditEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String eventId = UUID.randomUUID().toString();

    @Column(nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();

    private String sessionId;
    private String orderId;
    private String paymentId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuditEventType eventType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActorType actor;

    @Column(nullable = false, length = 1000)
    private String description;

    @Column(precision = 12, scale = 2)
    private BigDecimal amount;

    private boolean success = true;

    // Structured JSON payload holding safety check matrix, tool calls, price recalculation diffs, etc.
    @Column(columnDefinition = "TEXT")
    private String metadataJson;

    public AuditEvent() {}

    public AuditEvent(String sessionId, String orderId, AuditEventType eventType, ActorType actor, String description, BigDecimal amount, boolean success, String metadataJson) {
        this.eventId = UUID.randomUUID().toString();
        this.timestamp = LocalDateTime.now();
        this.sessionId = sessionId;
        this.orderId = orderId;
        this.eventType = eventType;
        this.actor = actor;
        this.description = description;
        this.amount = amount;
        this.success = success;
        this.metadataJson = metadataJson;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }
    public AuditEventType getEventType() { return eventType; }
    public void setEventType(AuditEventType eventType) { this.eventType = eventType; }
    public ActorType getActor() { return actor; }
    public void setActor(ActorType actor) { this.actor = actor; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getMetadataJson() { return metadataJson; }
    public void setMetadataJson(String metadataJson) { this.metadataJson = metadataJson; }
}