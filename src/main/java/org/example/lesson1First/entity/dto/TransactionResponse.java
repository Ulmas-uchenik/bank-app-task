package org.example.lesson1First.entity.dto;

import org.example.lesson1First.enums.TypeTransaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponse(
        String id,
        BigDecimal amount,
        TypeTransaction type,
        LocalDateTime date,
        String sourceId,
        String targetId
) {
}
