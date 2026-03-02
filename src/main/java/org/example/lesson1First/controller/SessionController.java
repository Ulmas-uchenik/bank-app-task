package org.example.lesson1First.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.lesson1First.entity.db.UserPassword;
import org.example.lesson1First.exception.NotFoundUserException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/session")
@RequiredArgsConstructor
public class SessionController {

    private final SessionRegistry sessionRegistry;

    @GetMapping("/logout")
    public void logout(HttpServletRequest request) {
        request.getSession().invalidate(); // Уничтожает сессию
    }

    @GetMapping("/logout-all")
    public void logoutAll(@AuthenticationPrincipal UserPassword principal) {
        sessionRegistry.getAllSessions(principal, false).forEach(it -> it.expireNow());
    }

    @GetMapping("/active")
    public ResponseEntity<?> active(@AuthenticationPrincipal UserPassword principal) {
        List<Map<String, String>> allSessions = sessionRegistry.getAllSessions(principal, false).stream().map(it ->
                Map.of(
                        "SessionId: ", it.getSessionId(),
                        "Username", principal.getUsername() ,
                        "LastRequest: ", it.getLastRequest().toString()
                )
        ).toList();

        return ResponseEntity.ok(allSessions);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/logout-all/{userEmail}")
    public ResponseEntity<?> logoutAllByUserEmail(@PathVariable(name = "userEmail") String email) {
        UserPassword userPassword = sessionRegistry.getAllPrincipals().stream().map(it -> (UserPassword) it)
                .filter(it -> it.getUsername().equals(email)).findFirst()
                .orElseThrow(() -> new NotFoundUserException("Нету пользователя у которого активные сессии с email " + email));

        List<SessionInformation> allSessions = sessionRegistry.getAllSessions(userPassword, false);
        allSessions.forEach(SessionInformation::expireNow);

        return ResponseEntity.ok(Map.of("session expired", allSessions.size()));
    }
}
