package org.example.lesson1First.entity.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record DepositRequest (
    @Min(value = 1, message = "Сумма должна быть больше нуля")
    @Pattern(regexp = "^[1-9]\\d*$", message = "Сумма должна быть целым числом больше нуля")
    String amount
){}
