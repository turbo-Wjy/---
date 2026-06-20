package com.example.ailearning.module.fusion.service;

import com.example.ailearning.common.exception.BusinessException;
import com.example.ailearning.common.exception.ErrorCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

@Service
class JsonValueService {
    private final ObjectMapper objectMapper;

    JsonValueService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    String toJson(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof String stringValue) {
            String trimmed = stringValue.trim();
            return trimmed.isBlank() ? null : trimmed;
        }
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "JSON 字段格式不正确");
        }
    }
}
