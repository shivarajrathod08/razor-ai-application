package com.hackathon.dto;



import com.hackathon.model.AuditEvent;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AuditEventDto {
    private String eventId;
    private LocalDateTime timestamp;
    private String sessionId;
    private String orderId;
    private String paymentId;
    private String eventType;
    private String actor;
    private String description;
    private BigDecimal amount;
    private boolean success;
    private String metadataJson;

    public AuditEventDto() {}

    public static AuditEventDto fromEntity(AuditEvent e) {
        AuditEventDto dto = new AuditEventDto();
        dto.setEventId(e.getEventId());
        dto.setTimestamp(e.getTimestamp());
        dto.setSessionId(e.getSessionId());
        dto.setOrderId(e.getOrderId());
        dto.setPaymentId(e.getPaymentId());
        dto.setEventType(e.getEventType().name());
        dto.setActor(e.getActor().name());
        dto.setDescription(e.getDescription());
        dto.setAmount(e.getAmount());
        dto.setSuccess(e.isSuccess());
        dto.setMetadataJson(e.getMetadataJson());
        return dto;
    }

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
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public String getActor() { return actor; }
    public void setActor(String actor) { this.actor = actor; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getMetadataJson() { return metadataJson; }
    public void setMetadataJson(String metadataJson) { this.metadataJson = metadataJson; }
}