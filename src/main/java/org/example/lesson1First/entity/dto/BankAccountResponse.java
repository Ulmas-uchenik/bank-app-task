package org.example.lesson1First.entity.dto;

public record BankAccountResponse(
        String bankAccountId,
        String user_id,
        String balance
) {
}
