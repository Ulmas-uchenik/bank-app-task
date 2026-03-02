package org.example.lesson1First.entity.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record BankAccountDto(
        @NotEmpty(message = "Поле не может быть пустым")
        @Size(min = 4, message = "Поле должно быть минимум 8 символов в длину")
        String accountNumber
) {
}