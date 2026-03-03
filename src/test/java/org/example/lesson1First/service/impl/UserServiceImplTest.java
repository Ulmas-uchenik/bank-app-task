package org.example.lesson1First.service.impl;

import org.example.lesson1First.entity.db.BankAccount;
import org.example.lesson1First.entity.db.Transaction;
import org.example.lesson1First.entity.db.User;
import org.example.lesson1First.entity.dto.BankAccountRepositoryRequest;
import org.example.lesson1First.entity.dto.UserDto;
import org.example.lesson1First.entity.dto.UserSummaryDto;
import org.example.lesson1First.enums.Currency;
import org.example.lesson1First.enums.TypeTransaction;
import org.example.lesson1First.exception.NotFoundUserException;
import org.example.lesson1First.exception.NotUniqueUserIdException;
import org.example.lesson1First.repository.BankAccountRepository;
import org.example.lesson1First.repository.TransactionRepository;
import org.example.lesson1First.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BankAccountRepository bankAccountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private UserServiceImpl userService; // Предполагаем, что имя реализации - UserServiceImpl

    private User testUser;
    private User testUser2;
    private BankAccount testBankAccount;
    private BankAccount testBankAccount2;
    private Transaction testTransaction;
    private UserDto testUserDto;
    private BankAccountRepositoryRequest testBankAccountRequest;

    @BeforeEach
    void setUp() {
        // Инициализация тестовых данных
        testUser = new User();
        testUser.setId("user123");
        testUser.setUsername("testUser");
        testUser.setEmail("test@example.com");
        testUser.setPhone("+71234567890");


        testUser2 = new User();
        testUser2.setId("user456");
        testUser2.setUsername("testUser2");
        testUser2.setEmail("test2@example.com");
        testUser2.setPhone("+79876543210");

        testBankAccount = BankAccount.builder()
                .number("ACC123")
                .balance(new BigDecimal("1000.00"))
                .currency(Currency.USD)
                .isBlocking(false)
                .owner(testUser)
                .transactionsWhereImSource(new HashSet<>())
                .transactionsWhereImTarget(new HashSet<>())
                .build();

        testBankAccount2 = BankAccount.builder()
                .number("ACC456")
                .balance(new BigDecimal("500.00"))
                .currency(Currency.EUR)
                .isBlocking(false)
                .owner(testUser2)
                .transactionsWhereImSource(new HashSet<>())
                .transactionsWhereImTarget(new HashSet<>())
                .build();

        testTransaction = new Transaction();
        testTransaction.setId("trans123");
        testTransaction.setAmount(new BigDecimal("100.00"));
        testTransaction.setType(TypeTransaction.TRANSFER);
        testTransaction.setDate(Timestamp.valueOf(LocalDateTime.now()));
        testTransaction.setSourceBankAccount(testBankAccount);
        testTransaction.setTargetBankAccount(testBankAccount2);

        testUserDto = new UserDto("user123", "testUser", "test@example.com", "+71234567890");

        testBankAccountRequest = new BankAccountRepositoryRequest(
                "user123", "NEWACC123", "USD"
        );
    }

    @Test
    void addUser_Success() {
        // Arrange
        when(userRepository.existsById(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        userService.addUser(testUserDto);

        // Assert
        verify(userRepository).existsById("user123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void addUser_UserAlreadyExists_ThrowsException() {
        // Arrange
        when(userRepository.existsById("user123")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> userService.addUser(testUserDto))
                .isInstanceOf(NotUniqueUserIdException.class);

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getUserById_Success() {
        // Arrange
        when(userRepository.findById("user123")).thenReturn(Optional.of(testUser));

        // Act
        User foundUser = userService.getUserById("user123");

        // Assert
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getId()).isEqualTo("user123");
        assertThat(foundUser.getUsername()).isEqualTo("testUser");
    }

    @Test
    void getUserById_NotFound_ThrowsException() {
        // Arrange
        when(userRepository.findById("user123")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userService.getUserById("user123"))
                .isInstanceOf(NotFoundUserException.class);
    }

    @Test
    void getAllUsers_Success() {
        // Arrange
        List<User> users = Arrays.asList(testUser, testUser2);
        when(userRepository.findAll()).thenReturn(users);

        // Act
        List<User> foundUsers = userService.getAllUsers();

        // Assert
        assertThat(foundUsers).hasSize(2);
        assertThat(foundUsers).containsExactly(testUser, testUser2);
    }

    @Test
    void deleteUser_Success() {
        // Arrange
        when(userRepository.existsById("user123")).thenReturn(true);
        doNothing().when(userRepository).deleteById("user123");

        // Act
        userService.deleteUser("user123");

        // Assert
        verify(userRepository).deleteById("user123");
    }

    @Test
    void deleteUser_NotFound_ThrowsException() {
        // Arrange
        when(userRepository.existsById("user123")).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> userService.deleteUser("user123"))
                .isInstanceOf(NotFoundUserException.class);
    }

    @Test
    void getSummaryById_Success() {
        // Arrange
        List<BankAccount> bankAccounts = Arrays.asList(
                BankAccount.builder()
                        .number("ACC1")
                        .balance(new BigDecimal("1000"))
                        .isBlocking(false)
                        .build(),
                BankAccount.builder()
                        .number("ACC2")
                        .balance(new BigDecimal("2000"))
                        .isBlocking(false)
                        .build(),
                BankAccount.builder()
                        .number("ACC3")
                        .balance(new BigDecimal("500"))
                        .isBlocking(true)
                        .build()
        );

        testUser.setBankAccounts(bankAccounts);

        when(userRepository.findById("user123")).thenReturn(Optional.of(testUser));

        // Act
        UserSummaryDto summary = userService.getSummaryById("user123");

        // Assert
        assertThat(summary).isNotNull();
        assertThat(summary.getUserId()).isEqualTo("user123");
        assertThat(summary.getName()).isEqualTo("testUser");
        assertThat(summary.getTotalBalance()).isEqualTo(new BigDecimal("3500"));
        assertThat(summary.getAccountCount()).isEqualTo(3);
        assertThat(summary.getActiveAccount()).isEqualTo(2);
        assertThat(summary.getBlockedAccount()).isEqualTo(1);
    }

    @Test
    void createAccount_Success() {
        // Arrange
        when(userRepository.findById("user123")).thenReturn(Optional.of(testUser));
        when(bankAccountRepository.existsById("NEWACC123")).thenReturn(false);
        when(bankAccountRepository.save(any(BankAccount.class))).thenReturn(testBankAccount);

        // Act
        userService.createAccount(testBankAccountRequest);

        // Assert
        verify(bankAccountRepository).save(any(BankAccount.class));
    }

    @Test
    void createAccount_AccountAlreadyExists_ThrowsException() {
        // Arrange
        when(bankAccountRepository.existsById("NEWACC123")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> userService.createAccount(testBankAccountRequest))
                .isInstanceOf(NotUniqueUserIdException.class);
    }

    @Test
    void getAllUsersBankAccount_Success() {
        // Arrange
        List<BankAccount> accounts = Arrays.asList(testBankAccount, testBankAccount2);
        testUser.setBankAccounts(accounts);
        when(userRepository.findById("user123")).thenReturn(Optional.of(testUser));

        // Act
        List<BankAccount> foundAccounts = userService.getAllUsersBankAccount("user123");

        // Assert
        assertThat(foundAccounts).hasSize(2);
    }

}