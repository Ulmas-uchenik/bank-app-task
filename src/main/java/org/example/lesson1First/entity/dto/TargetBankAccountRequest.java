package org.example.lesson1First.entity.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

public record TargetBankAccountRequest(
        @NotEmpty(message = "Поле не должно быть пустым")
        String userId,
        @NotEmpty(message = "Поле не должно быть пустым")
        String bankId,
        @Pattern(regexp = "^[1-9]\\d*$", message = "Сумма должна быть целым числом больше нуля")
        String amount
) {
}
