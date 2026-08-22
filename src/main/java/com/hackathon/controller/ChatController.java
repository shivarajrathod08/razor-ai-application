package com.hackathon.controller;



import com.hackathon.dto.ApiResponse;
import com.hackathon.dto.ChatRequest;
import com.hackathon.dto.ChatResponse;
import com.hackathon.service.AiCommerceAgentService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
public class ChatController {
    private final AiCommerceAgentService aiService;

    public ChatController(AiCommerceAgentService aiService) {
        this.aiService = aiService;
    }

    @PostMapping
    public ApiResponse<ChatResponse> chat(@Valid @RequestBody ChatRequest request) {
        return ApiResponse.ok(aiService.processUserMessage(request));
    }
}