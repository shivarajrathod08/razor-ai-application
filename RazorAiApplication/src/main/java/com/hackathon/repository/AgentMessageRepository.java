package com.hackathon.repository;


import com.hackathon.model.AgentMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AgentMessageRepository extends JpaRepository<AgentMessage, Long> {
    List<AgentMessage> findBySessionIdOrderByTimestampAsc(Long sessionId);
}