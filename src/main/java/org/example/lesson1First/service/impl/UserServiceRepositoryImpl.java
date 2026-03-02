package org.example.lesson1First.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.example.lesson1First.entity.db.BankAccount;
import org.example.lesson1First.entity.db.Transaction;
import org.example.lesson1First.entity.db.User;
import org.example.lesson1First.entity.dto.*;
import org.example.lesson1First.entity.dto.mapper.TransactionMapper;
import org.example.lesson1First.enums.Currency;
import org.example.lesson1First.enums.TypeTransaction;
import org.example.lesson1First.exception.*;
import org.example.lesson1First.repository.BankAccountRepository;
import org.example.lesson1First.repository.TransactionRepository;
import org.example.lesson1First.repository.UserRepository;
import org.example.lesson1First.service.UserServiceRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Service
@Log4j2
@RequiredArgsConstructor
public class UserServiceRepositoryImpl implements UserServiceRepository {

    private final UserRepository userRepository;
    private final BankAccountRepository bankAccountRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    @Override
    public void addUser(UserDto u) throws NotUniqueUserIdException {
        log.info("Добавили пльзователя");
        if (userRepository.findById(u.id()).isPresent()) {
            log.error("Добавили пльзователя - ERROR");
            throw new NotUniqueUserIdException("Пользователь с id " + u.id() + " уже существует");
        }

        userRepository.save(new User(u.id(), u.name(), u.phone(), u.email(), null));
    }

    @Override
    public User getUserById(String userId) throws NotFoundUserException {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundUserException("Пользователь с id = %s. Несуществует".formatted(userId)));
    }

    // TODO доделать эту функцию
    @Override
    public UserSummaryDto getSummaryById(String userId) throws NotFoundUserException {
        User user = getUserById(userId);
        List<BankAccount> bankAccounts = user.getBankAccounts();

        UserSummaryDto summary = new UserSummaryDto();
        summary.setName(user.getUsername());
        summary.setUserId(userId);
        BigDecimal totalBalance = BigDecimal.ZERO;

        for (BankAccount bankAccount : bankAccounts) {
            summary.increateAccountCount();
            totalBalance = totalBalance.add(bankAccount.getBalance());

            if (bankAccount.getIsBlocking()) {
                summary.incrementBlockingAccountCount();
            } else {
                summary.incrementActiveAccountCount();
            }
        }
        summary.setTotalBalance(totalBalance);

        return summary;
    }

    @Override
    public List<User> getAllUsers() {
        log.info("Получили список пользователей");

        return userRepository.findAll();
    }

    @Override
    public void deleteUser(String userId) throws NotFoundUserException {
        User user = this.getUserById(userId);
        userRepository.delete(user);
    }

    @Override
    public void createAccount(BankAccountRepositoryRequest b) throws NotFoundUserException, NotUniqueUserIdException {
        if (bankAccountRepository.findById(b.number()).isPresent()) {
            throw new NotUniqueUserIdException("Такой банковский аккаунт с id " + b.number() + " уже существует, пожалуйста выберите другой");
        }

        User user = getUserById(b.userId());
        BankAccount entity = new BankAccount(b.number(), BigDecimal.ZERO, Currency.valueOf(b.currency().toUpperCase()), user, null, null);
        bankAccountRepository.save(entity);
    }

    @Override
    @Transactional
    public void deleteAccount(BankAccountRepositoryRequest b) throws NotFoundUserException, NotUniqueUserIdException {
        BankAccount forRemoveBankAccount = getUserBankAccountById(b.userId(), b.number());
        String number = forRemoveBankAccount.getNumber();
        bankAccountRepository.changeUserIdOnRemoved(number);
    }

    @Override
    public List<BankAccount> getAllUsersBankAccount(String userId) throws NotFoundUserException {
        User user = getUserById(userId);
        return user.getBankAccounts();
    }

    @Override
    public BankAccount getUserBankAccountById(String userId, String bankId) throws NotFoundUserException, NotFoundUserBankAccountException {
        User user = getUserById(userId);
        return user.getBankAccounts().stream().filter(it -> it.getNumber().equals(bankId)).findFirst()
                .orElseThrow(() -> new NotFoundUserBankAccountException("Аккаун " + bankId + " не был найден, пожалуйста проверьте валидность данных"));
    }

    @Override
    public BankAccount getAccountById(String bankAccountId) throws NotFoundUserException, NotFoundUserBankAccountException {
        return bankAccountRepository.findById(bankAccountId).orElseThrow(() -> new NotFoundUserBankAccountException("Аккаун " + bankAccountId + " не был найден, пожалуйста проверьте валидность данных"));
    }


    @Override
    @Transactional
    public String depositInUserAccount(String userId, String bankAccountId, String amount) throws NotFoundUserException, NotFoundUserBankAccountException {
        BankAccount bankAccount = getUserBankAccountById(userId, bankAccountId);
        checkBlocking(bankAccount);

        BigDecimal balance = bankAccount.getBalance();
        BigDecimal amountBigDecimal = new BigDecimal(amount);
        BigDecimal resultBalance = balance.add(amountBigDecimal);
        bankAccount.setBalance(resultBalance);

        Transaction transaction = new Transaction(UUID.randomUUID().toString(), amountBigDecimal,
                TypeTransaction.DEPOSIT, Timestamp.valueOf(LocalDateTime.now()), null, bankAccount);
        transactionRepository.save(transaction);

        return resultBalance.toString();
    }
    @Override
    @Transactional
    public String withdrawInUserAccount(String userId, String bankAccountId, String amount) throws Exception {
        BankAccount bankAccount = getUserBankAccountById(userId, bankAccountId);
        checkBlocking(bankAccount);

        BigDecimal balance = bankAccount.getBalance();
        BigDecimal amountBigDecimal = new BigDecimal(amount);
        BigDecimal resultBalance = balance.subtract(amountBigDecimal);

        if (resultBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new NotHaveEnoughBalance("У пользователя не хватает денег на счету для списания. Вы можете списать только " + balance + " " + bankAccount.getCurrency());
        }
        bankAccount.setBalance(resultBalance);

        Transaction transaction = new Transaction(UUID.randomUUID().toString(), amountBigDecimal,
                TypeTransaction.WITHDRAW, Timestamp.valueOf(LocalDateTime.now()), null, bankAccount);
        transactionRepository.save(transaction);

        return resultBalance.toString();
    }

    @Override
    @Transactional
    public void blockUserAccount(String userId, String bankAccountId) {
        BankAccount bankAccount = getUserBankAccountById(userId, bankAccountId);
        bankAccount.setIsBlocking(!bankAccount.getIsBlocking());
    }

    @Override
    @Transactional
    public String transferInUserAccountToAnotherUserAccount(String sourceUserId, String sourceBankAccountId, TargetBankAccountRequest target) throws Exception {
        BankAccount sourceBankAccount = getUserBankAccountById(sourceUserId, sourceBankAccountId);
        BankAccount targetBankAccount = getUserBankAccountById(target.userId(), target.bankId());
        BigDecimal amountBigDecimal = new BigDecimal(target.amount());

        BigDecimal sourceBankAccountBalance = sourceBankAccount.getBalance();
        BigDecimal targetBankAccountBalance = targetBankAccount.getBalance();

        checkBlocking(sourceBankAccount);
        checkBlocking(targetBankAccount);

        if (sourceBankAccountBalance.subtract(amountBigDecimal).compareTo(BigDecimal.ZERO) < 0) {
            throw new NotHaveEnoughBalance("У пользователя не хватает денег на счету для списания. Вы можете списать только " + sourceBankAccountBalance + " " + sourceBankAccount.getCurrency());
        }

        sourceBankAccount.setBalance(sourceBankAccountBalance.subtract(amountBigDecimal));

        BigDecimal resultBalance = targetBankAccountBalance.add(amountBigDecimal);
        targetBankAccount.setBalance(resultBalance);

        Transaction transaction = new Transaction(UUID.randomUUID().toString(), amountBigDecimal,
                TypeTransaction.DEPOSIT, Timestamp.valueOf(LocalDateTime.now()), sourceBankAccount, targetBankAccount);
        transactionRepository.save(transaction);

        return resultBalance.toString();
    }

    @Override
    public List<TransactionResponse> getAllTransactionsFromBankAccount(String userId, String bankAccountId) throws NotFoundUserBankAccountException, NotFoundUserException {
        BankAccount bankAccount = getUserBankAccountById(userId, bankAccountId);
        Stream<Transaction> transactionStream = Stream.concat(bankAccount.getTransactionsWhereImSource().stream(), bankAccount.getTransactionsWhereImTarget().stream());
        return transactionStream.sorted(Comparator.comparing(Transaction::getDate).reversed()).map(transactionMapper::toResponse).toList();
    }

    @Override
    public Page<Transaction> getAllBankAccountTransactionsPageable(String userId, String bankAccountId, Integer pageSize, Integer pageCount) throws NotFoundUserException {
        getUserBankAccountById(userId, bankAccountId);
        return transactionRepository.findAllBy(bankAccountId, PageRequest.of(pageCount, pageSize));
    }

    @Override
    public Page<Transaction> getAllBankAccountTransactionsPageable(String userId, String bankAccountId, TypeTransaction type, Integer pageSize, Integer pageCount) throws NotFoundUserException {
        getUserBankAccountById(userId, bankAccountId);
        return transactionRepository.findAllBy(bankAccountId, type, PageRequest.of(pageCount, pageSize));
    }

    static void checkBlocking(BankAccount bankAccount) {
        if (bankAccount.getIsBlocking()) {
            throw new UserAccountIsBlockingException("Аккаунт " + bankAccount.getNumber() + " был заблокирован, операции по этому аккаунты не возможны. Пожалуйста обратитесь в поддержку с вашим вопросом, чтобы вам помогли");
        }
    }
}
