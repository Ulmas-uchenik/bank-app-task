package org.example.lesson1First.controller;

import org.example.lesson1First.entity.dto.*;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WithMockUser(username = "UserAdmin", roles = "ADMIN")
class BaseIntegrationIntegrationTest extends BaseIntegrationTest {

    @Test
    void getAllUsers_ShouldReturnAllUsers() throws Exception {
        mockMvc.perform(get("/api/v3/accounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].id").value("test-user-1"))
                .andExpect(jsonPath("$[0].username").value("testuser1"));
    }

    @Test
    void createUser_ShouldCreateNewUser() throws Exception {
        UserDto newUser = new UserDto(
                "test-user-4",
                "newuser",
                "newuser@example.com",
                "+79998887766"
        );

        mockMvc.perform(post("/api/v3/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(newUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("test-user-4"))
                .andExpect(jsonPath("$.name").value("newuser"));

        // Проверяем, что пользователь действительно создан
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM users WHERE user_id = 'test-user-4'",
                Integer.class
        );
        assertThat(count).isEqualTo(1);
    }

    @Test
    void createUser_WithDuplicateId_ShouldReturnError() throws Exception {
        UserDto duplicateUser = new UserDto(
                "test-user-1",  // Существующий ID
                "duplicate",
                "duplicate@example.com",
                "+71112223344"
        );

        mockMvc.perform(post("/api/v3/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(duplicateUser)))
                .andExpect(status().is4xxClientError());
    }


    @Test
    void getUserSummary_ShouldReturnCorrectSummary() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/v3/accounts/{userId}/summary", "test-user-1"))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        assertThat(jsonResponse).contains("test-user-1");
        assertThat(jsonResponse).contains("testuser1");
        assertThat(jsonResponse).contains("1500.00"); // 1000 + 500
    }

    @Test
    void depositAccount_ShouldIncreaseBalance() throws Exception {
        DepositRequest deposit = new DepositRequest("200");

        mockMvc.perform(post("/api/v3/accounts/{id}/{bankId}/deposit", "test-user-1", "ACC-TEST-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(deposit)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value("1200.00"));

        // Проверяем обновленный баланс
        BigDecimal balance = jdbcTemplate.queryForObject(
                "SELECT balance FROM bank_accounts WHERE number = 'ACC-TEST-1'",
                BigDecimal.class
        );
        assertThat(balance).isEqualTo(new BigDecimal("1200.00"));
    }

    @Test
    void withdrawAccount_ShouldDecreaseBalance() throws Exception {
        WithdrawRequest withdraw = new WithdrawRequest("300");

        mockMvc.perform(post("/api/v3/accounts/{id}/{bankId}/withdraw", "test-user-1", "ACC-TEST-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(withdraw)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value("700.00"));

        // Проверяем обновленный баланс
        BigDecimal balance = jdbcTemplate.queryForObject(
                "SELECT balance FROM bank_accounts WHERE number = 'ACC-TEST-1'",
                BigDecimal.class
        );
        assertThat(balance).isEqualTo(new BigDecimal("700.00"));
    }

    @Test
    void withdrawAccount_InsufficientFunds_ShouldReturnError() throws Exception {
        WithdrawRequest withdraw = new WithdrawRequest("2000"); // Больше чем на счете

        mockMvc.perform(post("/api/v3/accounts/{id}/{bankId}/withdraw", "test-user-1", "ACC-TEST-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(withdraw)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void blockAccount_ShouldBlockAccount() throws Exception {
        mockMvc.perform(post("/api/v3/accounts/{id}/{bankId}/block", "test-user-1", "ACC-TEST-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("successful"));

        // Проверяем, что счет заблокирован
        Boolean isBlocked = jdbcTemplate.queryForObject(
                "SELECT is_blocking FROM bank_accounts WHERE number = 'ACC-TEST-1'",
                Boolean.class
        );
        assertThat(isBlocked).isTrue();
    }

    @Test
    void getAccountTransactions_ShouldReturnTransactions() throws Exception {
        mockMvc.perform(get("/api/v3/accounts/{id}/{bankId}/transactions", "test-user-1", "ACC-TEST-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2)) // Две транзакции для ACC-TEST-1
                .andExpect(jsonPath("$[0].sourceId").value("ACC-TEST-1"));
    }

    @Test
    void getAccountTransactionsPageable_ShouldReturnPagedTransactions() throws Exception {
        mockMvc.perform(get("/api/v3/accounts/{id}/{bankId}/transactions/page", "test-user-1", "ACC-TEST-1")
                        .param("pageSize", "1")
                        .param("pageCount", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.totalElements").value(2));
    }



    @Test
    void getBankAccountInUser_ShouldReturnAccount() throws Exception {
        mockMvc.perform(get("/api/v3/accounts/{id}/{bankId}", "test-user-1", "ACC-TEST-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value("ACC-TEST-1"))
                .andExpect(jsonPath("$.balance").value(1000.00))
                .andExpect(jsonPath("$.currency").value("USD"));
    }

    @Test
    void getBankAccountInUser_WithWrongUser_ShouldReturnError() throws Exception {
        mockMvc.perform(get("/api/v3/accounts/{id}/{bankId}", "test-user-2", "ACC-TEST-1"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void getAllUsersBankAccount_ShouldReturnUserAccounts() throws Exception {
        mockMvc.perform(get("/api/v3/accounts/{id}", "test-user-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].number").value("ACC-TEST-1"))
                .andExpect(jsonPath("$[1].number").value("ACC-TEST-2"));
    }
}