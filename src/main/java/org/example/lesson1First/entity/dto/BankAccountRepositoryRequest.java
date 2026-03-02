package org.example.lesson1First.entity.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import org.example.lesson1First.entity.db.BankAccount;
import org.example.lesson1First.enums.Currency;

/**
 * DTO for {@link BankAccount}
 */
public record BankAccountRepositoryRequest(
        @NotEmpty(message = "Поле не может быть пустым")
        @Size(min = 4, message = "Поле должно быть минимум 8 символов в длину")
        String userId,
        @NotEmpty(message = "Поле не может быть пустым")
        String number,
        @Size(min = 3, max = 3, message = "Поле должно быть 3 заглавных символа в длину обязательно!")
        @NotEmpty(message = "Поле не может быть пустым")
        String currency) {
}