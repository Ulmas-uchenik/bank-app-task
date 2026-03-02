package org.example.lesson1First.entity.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record WithdrawRequest(
        @NotNull(message = "Поле должно быть проинициализированно")
        @Pattern(regexp = "^[1-9]\\d*$", message = "Сумма должна быть целым числом больше нуля")
        String amount
) {

}
