package org.example.lesson1First.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.lesson1First.entity.db.User;
import org.example.lesson1First.entity.db.UserPassword;
import org.example.lesson1First.exception.database.UniqueUserEmailException;
import org.example.lesson1First.exception.superClasses.UserInputException;
import org.example.lesson1First.repository.UserPasswordRepository;
import org.example.lesson1First.repository.UserRepository;
import org.example.lesson1First.service.JwtService;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class LoginController {

    private final UserPasswordRepository userPasswordRepository;

    private final JwtService jwtService;

    private final PasswordEncoder passwordEncoder;

    @GetMapping("/public")
    public String publicController() {
        return "<h1>Hello world this is how are you in the this world</h1>";
    }
    @GetMapping
    public String securedEndpoint() {
        return "<h1 style=\"color:red;font-size=20em;\">Hello world this is how are you in the this world</h1>";
    }
    @GetMapping("/session-info")
    public String getSessionInfo(HttpSession session) {
        // 1. Максимальное время жизни (например, 1800 секунд = 30 мин)
        long maxInactiveIntervalSeconds = session.getMaxInactiveInterval();

        // 2. Сколько времени прошло с последнего запроса (в секундах)
        long lastAccessedTimeMs = session.getLastAccessedTime();
        long currentTimeMs = System.currentTimeMillis();
        long timePassedSeconds = (currentTimeMs - lastAccessedTimeMs) / 1000;

        // 3. Расчет остатка
        long remainingTimeSeconds = maxInactiveIntervalSeconds - timePassedSeconds;

        return "Сессия истечет через: " + remainingTimeSeconds + " секунд";
    }


    @PostMapping("/login/token")
    public ResponseEntity<?> accessToken(
            @RequestBody UserPassword userPassword
    ) {
        if (userPasswordRepository.findByEmail(userPassword.getEmail()).isPresent())
            throw new UniqueUserEmailException("Пользователь с email " + userPassword.getEmail() + " уже существует, пожалуйста выберите другой email");

        String encodePassword = passwordEncoder.encode(userPassword.getPassword());
        userPassword.setPassword(encodePassword);

        userPasswordRepository.save(userPassword);

        String accessToken = jwtService.generateToken(userPassword);

        return ResponseEntity.ok(Map.of("accessToken", accessToken));
    }

    @PostMapping("/login/login")
    public ResponseEntity<?> login(
            @RequestBody UserPassword userPassword
    ) {
        Optional<UserPassword> repositoryUserOptional = userPasswordRepository.findByEmail(userPassword.getEmail());
        if (repositoryUserOptional.isEmpty())
            throw new UniqueUserEmailException("Пользователь с email " + userPassword.getEmail() + " не существует, вы не можете войти в свой аккаунт");
        UserPassword repositoryUser = repositoryUserOptional.get();

        if (!passwordEncoder.matches(userPassword.getPassword(), repositoryUser.getPassword())) {
            throw new UserInputException("Вы ввели не верный пароль");
        }

        String accessToken = jwtService.generateToken(repositoryUser);
        return ResponseEntity.ok(Map.of("accessToken", accessToken));
    }

}