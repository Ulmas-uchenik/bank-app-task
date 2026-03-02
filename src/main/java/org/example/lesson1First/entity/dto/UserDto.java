package org.example.lesson1First.entity.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

public record UserDto(@NotEmpty(message = "Поле не может быть пустым") String id,
                      @NotEmpty(message = "Поле не может быть пустым") @Size(min = 2, max = 12, message = "Поле должно содержать от 2 до 12 символов") String name,
                      @Email(message = "Не валидный Email") String email,
                      @Pattern(regexp = "^\\+7\\d{10}$", message = "Phone number must be in format +7XXXXXXXXXX") String phone) {
}