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
    void addUser(UserDto userDto) throws NotUniqueUserIdException;
    User getUserById(String userId) throws NotFoundUserException;
    List<User> getAllUsers();
    void deleteUser(String userId) throws NotFoundUserException;
    UserSummaryDto getSummaryById(String userId) throws NotFoundUserException;

    void createAccount(BankAccountRepositoryRequest bankAccount) throws NotFoundUserException, NotUniqueUserIdException;
    void deleteAccount(BankAccountRepositoryRequest bankAccount) throws NotFoundUserException, NotUniqueUserIdException;
    List<BankAccount> getAllUsersBankAccount(String userId) throws NotFoundUserException;

    Page<Transaction> getAllBankAccountTransactionsPageable(String userId, String bankAccountId, Integer pageSize, Integer pageCount) throws NotFoundUserException;
    Page<Transaction> getAllBankAccountTransactionsPageable(String userId, String bankAccountId, TypeTransaction type, Integer pageSize, Integer pageCount) throws NotFoundUserException;

    BankAccount getUserBankAccountById(String userId, String bankId) throws NotFoundUserException, NotFoundUserBankAccountException;
    BankAccount getAccountById(String bankAccountId) throws NotFoundUserException, NotFoundUserBankAccountException;

    String depositInUserAccount(String userId, String bankAccountId, String amount) throws NotFoundUserException, NotFoundUserBankAccountException;
    String withdrawInUserAccount(String userId, String bankAccountId, String amount) throws Exception;
    void blockUserAccount(String userId, String bankAccountId);

    String transferInUserAccountToAnotherUserAccount(String sourceUserId, String sourceBankAccountId, TargetBankAccountRequest targetBankAccountRequest) throws Exception;
    List<TransactionResponse> getAllTransactionsFromBankAccount(String userId, String bankAccountId) throws NotFoundUserBankAccountException, NotFoundUserException;
}
