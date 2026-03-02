package org.example.lesson1First.entity.dto;

import jakarta.validation.constraints.Min;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UserSummaryDto {
    private String userId;
    private String name;
    private BigDecimal totalBalance;
    private int accountCount;
    private int activeAccount;
    private int blockedAccount;

    public void increateAccountCount(){
        this.accountCount++;
    }

    public void incrementActiveAccountCount(){
        this.activeAccount++;
    }
    public void incrementBlockingAccountCount(){
        this.blockedAccount++;
    }
}
