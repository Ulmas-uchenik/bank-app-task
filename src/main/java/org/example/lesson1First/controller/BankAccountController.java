package org.example.lesson1First.controller;

//import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.lesson1First.entity.dto.*;
import org.example.lesson1First.enums.TypeTransaction;
import org.example.lesson1First.exception.NotFoundUserBankAccountException;
import org.example.lesson1First.exception.NotFoundUserException;
import org.example.lesson1First.exception.NotUniqueUserIdException;
import org.example.lesson1First.service.UserServiceRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * <pre>
 *
 * 2. Реализуйте следующие эндпоинты:
 *     - `POST /accounts` — создать новый аккаунт
 *     - `GET /accounts/{id}` — получить данные аккаунта
 *     - `POST /accounts/{id}/deposit` — пополнить баланс
 *     - `POST /accounts/{id}/withdraw` — снять средства
 *     - `POST /transactions/transfer` — перевести между счетами
 *     - `GET /accounts/{id}/transactions` — получить историю операций
 * 3. Используйте:
 *     - `@RequestMapping`, `@GetMapping`, `@PostMapping`
 *     - `@RequestParam`, `@RequestBody`, `@PathVariable`
 * </pre>
 */
@RestController
@RequestMapping("api/v3/accounts")
//@Tag(name = "Пользователи", description = "API для управления пользователями")
public class BankAccountController {

    private final UserServiceRepository userService;


    public BankAccountController(UserServiceRepository userService){
        this.userService = userService;
    }


    @GetMapping
    public ResponseEntity<?> getAllUser() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody UserDto userDto) throws NotUniqueUserIdException {
        userService.addUser(userDto);

        return ResponseEntity.ok(userDto);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteUser(@Valid @RequestBody UserDto userDto) throws NotFoundUserException {
        userService.deleteUser(userDto.id());

        return ResponseEntity.ok(Map.of("sttaattuuss", "successful"));
    }

    @PostMapping("{userId}")
    public ResponseEntity<?> createAccount(@Valid @RequestBody BankAccountRepositoryRequest bankAccountRequest) throws NotFoundUserException, NotUniqueUserIdException {
        userService.createAccount(bankAccountRequest);

        return ResponseEntity.ok(Map.of("sttaattuuss", "successful"));
    }

    @GetMapping("{userId}/summary")
    public ResponseEntity<?> summary(@PathVariable("userId") String userId ){
        return ResponseEntity.ok(userService.getSummaryById(userId));
    }

    @DeleteMapping("{userId}")
    public ResponseEntity<?> closeBankAccount(@Valid @RequestBody BankAccountRepositoryRequest bankAccountRequest) throws NotFoundUserException, NotUniqueUserIdException {
        userService.deleteAccount(bankAccountRequest);

        return ResponseEntity.ok(Map.of("account wad deleted", "successful"));
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getAllUsersBankAccount(@PathVariable("id") String id) throws NotFoundUserException {
        return ResponseEntity.ok(userService.getAllUsersBankAccount(id));
    }

    // TODO
    @GetMapping("{id}/{bankId}")
    public ResponseEntity<?> getBankAccountInUser(@PathVariable("id") String id, @PathVariable("bankId") String bankId) throws NotFoundUserBankAccountException, NotFoundUserException {
        return ResponseEntity.ok(userService.getUserBankAccountById(id, bankId));
    }

    @PostMapping("{id}/{bankId}/deposit")
    public ResponseEntity<?> depositAccount(@PathVariable("id") String userId, @PathVariable("bankId") String bankId, @RequestBody @Valid DepositRequest depositRequest) throws NotFoundUserBankAccountException, NotFoundUserException {
        String resultBalance = userService.depositInUserAccount(userId, bankId, depositRequest.amount());

        return ResponseEntity.ok(Map.of("balance", resultBalance));
    }

    @PostMapping("{id}/{bankId}/withdraw")
    public ResponseEntity<?> withdrawAccount(@PathVariable("id") String userId, @PathVariable("bankId") String bankId, @RequestBody @Valid WithdrawRequest withdrawRequest) throws Exception {
        String resultBalance = userService.withdrawInUserAccount(userId, bankId, withdrawRequest.amount());

        return ResponseEntity.ok(Map.of("balance", resultBalance));
    }

    @PostMapping("{id}/{bankId}/block")
    public ResponseEntity<?> blockAccount(@PathVariable("id") String userId, @PathVariable("bankId") String bankId) throws Exception {
        userService.blockUserAccount(userId, bankId);

        return ResponseEntity.ok(Map.of("status", "successful"));
    }

    @PostMapping("{id}/{bankId}/transfer")
    public ResponseEntity<?> transferToAccount(@PathVariable("id") String userId, @PathVariable("bankId") String bankId, @RequestBody @Valid TargetBankAccountRequest targetRequest) throws Exception {
        String resultBalance = userService.transferInUserAccountToAnotherUserAccount(userId, bankId, targetRequest);

        return ResponseEntity.ok(Map.of("balance", resultBalance));
    }

    @GetMapping("{id}/{bankId}/transactions")
    public ResponseEntity<?> getAccountTransactions(@PathVariable("id") String id, @PathVariable("bankId") String bankId) throws NotFoundUserBankAccountException, NotFoundUserException {
        return ResponseEntity.ok(userService.getAllTransactionsFromBankAccount(id, bankId));
    }

    @GetMapping("{id}/{bankId}/transactions/page")
    public ResponseEntity<?> getAccountTransactions(
            @PathVariable("id") String id,
            @PathVariable("bankId") String bankId,
            @RequestParam(name = "pageCount", defaultValue = "0") Integer pageCount,
            @RequestParam(name = "pageSize", defaultValue = "5") Integer pageSize,
            @RequestParam(name = "type", required = false) TypeTransaction type
    ) throws NotFoundUserBankAccountException, NotFoundUserException {
        if (type != null) {
            return ResponseEntity.ok(userService.getAllBankAccountTransactionsPageable(id, bankId, type, pageSize, pageCount));
        }
        return ResponseEntity.ok(userService.getAllBankAccountTransactionsPageable(id, bankId, pageSize, pageCount));
    }
}