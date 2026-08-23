package com.hackathon.controller;


import com.hackathon.config.AppProperties;
import com.hackathon.dto.ApiResponse;
import com.hackathon.dto.ConfigStatusDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/config")
public class ConfigController {
    private final AppProperties appProperties;

    public ConfigController(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    @GetMapping("/status")
    public ApiResponse<ConfigStatusDto> getConfigStatus() {
        ConfigStatusDto dto = new ConfigStatusDto();
        String keyId = appProperties.getRazorpay().getKeyId();
        dto.setRazorpayConfigured(keyId != null && !keyId.trim().isEmpty());
        dto.setRazorpayKeyId(keyId); // Public Key ID for client checkout popup, NEVER Key Secret

        String geminiKey = appProperties.getGemini().getApiKey();
        dto.setGeminiConfigured(geminiKey != null && !geminiKey.trim().isEmpty());
        dto.setTestMode(appProperties.getRazorpay().isTestMode());
        dto.setMaxTransactionLimit(appProperties.getPayment().getMaxTransactionLimit().doubleValue());
        dto.setCurrency(appProperties.getPayment().getCurrency());
        return ApiResponse.ok(dto);
    }
}