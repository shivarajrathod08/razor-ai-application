package com.hackathon.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "agent_messages")
public class AgentMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id_fk", nullable = false)
    @JsonBackReference
    private AgentSession session;

    @Column(nullable = false)
    private String sender; // "USER", "AI", "SYSTEM"

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(columnDefinition = "TEXT")
    private String toolCallsJson;

    @Column(columnDefinition = "TEXT")
    private String cardsDataJson;

    private LocalDateTime timestamp = LocalDateTime.now();

    public AgentMessage() {}

    public AgentMessage(AgentSession session, String sender, String content, String toolCallsJson, String cardsDataJson) {
        this.session = session;
        this.sender = sender;
        this.content = content;
        this.toolCallsJson = toolCallsJson;
        this.cardsDataJson = cardsDataJson;
        this.timestamp = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public AgentSession getSession() { return session; }
    public void setSession(AgentSession session) { this.session = session; }
    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getToolCallsJson() { return toolCallsJson; }
    public void setToolCallsJson(String toolCallsJson) { this.toolCallsJson = toolCallsJson; }
    public String getCardsDataJson() { return cardsDataJson; }
    public void setCardsDataJson(String cardsDataJson) { this.cardsDataJson = cardsDataJson; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}