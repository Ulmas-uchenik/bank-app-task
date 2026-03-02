package org.example.lesson1First.entity.dto;

import org.example.lesson1First.entity.db.BankAccount;
import org.example.lesson1First.entity.db.User;
import org.example.lesson1First.enums.Currency;

import java.math.BigDecimal;

/**
 * DTO for {@link BankAccount}
 */
public record BankAccountRepositoryResponse(String number, BigDecimal balance, Currency currency, UserDto owner) {
    /**
     * DTO for {@link User}
     */
    public record UserDto(String id, String username, String phone, String email) {
    }
}