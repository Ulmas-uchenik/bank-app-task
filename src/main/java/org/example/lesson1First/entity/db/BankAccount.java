package org.example.lesson1First.entity.db;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.example.lesson1First.enums.Currency;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Builder
@Entity
@Table(name = "bank_accounts")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BankAccount {
    @Id
    @Column(name="number")
    private String number;

    @Column(name = "balance")
    private BigDecimal balance;

    @Column(name = "currency")
    @Enumerated(value = EnumType.STRING)
    private Currency currency;

    @Column(name = "is_blocking", nullable = false)
    private Boolean isBlocking = false;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User owner;

    @Builder.Default
    @JsonIgnore
    @OneToMany(mappedBy = "sourceBankAccount")
    private Set<Transaction> transactionsWhereImSource = new HashSet<>();

    @Builder.Default
    @JsonIgnore
    @OneToMany(mappedBy = "targetBankAccount")
    private Set<Transaction> transactionsWhereImTarget = new HashSet<>();

    public BankAccount(String number, BigDecimal balance, Currency currency, User owner, Set<Transaction> transactionsWhereImSource, Set<Transaction> transactionsWhereImTarget) {
        this.number = number;
        this.balance = balance;
        this.currency = currency;
        this.owner = owner;
        this.transactionsWhereImSource = transactionsWhereImSource;
        this.transactionsWhereImTarget = transactionsWhereImTarget;
    }

    @Override
    public String toString() {
        return "BankAccount{" +
               "number='" + number + '\'' +
               ", balance=" + balance +
               ", currency=" + currency +
               '}';
    }
}
