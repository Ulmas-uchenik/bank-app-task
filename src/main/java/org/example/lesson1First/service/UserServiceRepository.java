package org.example.lesson1First.service;

import org.example.lesson1First.entity.db.BankAccount;
import org.example.lesson1First.entity.db.Transaction;
import org.example.lesson1First.entity.db.User;
import org.example.lesson1First.entity.dto.*;
import org.example.lesson1First.enums.TypeTransaction;
import org.example.lesson1First.exception.NotFoundUserBankAccountException;
import org.example.lesson1First.exception.NotFoundUserException;
import org.example.lesson1First.exception.NotUniqueUserIdException;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UserServiceRepository {
    void addUser(UserDto userDto);
    User getUserById(String userId);
    List<User> getAllUsers();
    void deleteUser(String userId);
    UserSummaryDto getSummaryById(String userId);

    void createAccount(BankAccountRepositoryRequest bankAccount);
    void deleteAccount(BankAccountRepositoryRequest bankAccount);
    List<BankAccount> getAllUsersBankAccount(String userId);

    Page<Transaction> getAllBankAccountTransactionsPageable(String userId, String bankAccountId, Integer pageSize, Integer pageCount) ;
    Page<Transaction> getAllBankAccountTransactionsPageable(String userId, String bankAccountId, TypeTransaction type, Integer pageSize, Integer pageCount) ;

    BankAccount getUserBankAccountById(String userId, String bankId);
    BankAccount getAccountById(String bankAccountId);

    String depositInUserAccount(String userId, String bankAccountId, String amount);
    String withdrawInUserAccount(String userId, String bankAccountId, String amount);
    void blockUserAccount(String userId, String bankAccountId);

    String transferInUserAccountToAnotherUserAccount(String sourceUserId, String sourceBankAccountId, TargetBankAccountRequest targetBankAccountRequest);
    List<TransactionResponse> getAllTransactionsFromBankAccount(String userId, String bankAccountId);
}
