package org.example.lesson1First.entity.db;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.lesson1First.enums.TypeTransaction;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "transactions")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Transaction {
    @Id
    @Column(name = "id")
    @NotNull(message = "Id не можем быть пустым")
    private String id;

    @Column(name = "amount")
    @NotNull
    @DecimalMin(value = "0.01", message = "Вы должны сделать платеж/перевод на сумму не менее 0.01 валюты")
    private BigDecimal amount;

    @Column(name = "type")
    @Enumerated(value = EnumType.STRING)
    @NotNull(message = "Выберите валюту")
    private TypeTransaction type;

    @Column(name = "date")
    @NotNull(message = "Установите дату")
    private Timestamp date;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "source")
    private BankAccount sourceBankAccount;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "target")
    @NotNull(message = "targetAccount - обязательное поле")
    private BankAccount targetBankAccount;

    @Override
    public String toString() {
        return "Transaction{" +
               "id='" + id + '\'' +
               ", amount='" + amount + '\'' +
               ", type=" + type +
               ", date=" + date +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Objects.equals(id, that.id) && Objects.equals(amount, that.amount) && type == that.type && Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, amount, type, date);
    }

    public String getSourceBankAccountNumber(){
        if (sourceBankAccount == null) return null;
        return sourceBankAccount.getNumber();
    }
    public String getTargetBankAccountNumber(){
        if (targetBankAccount == null) return null;
        return targetBankAccount.getNumber();
    }
}
