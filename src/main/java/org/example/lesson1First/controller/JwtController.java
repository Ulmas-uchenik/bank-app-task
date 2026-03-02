package org.example.lesson1First.controller;

import lombok.RequiredArgsConstructor;
import org.example.lesson1First.entity.db.TokenEntity;
import org.example.lesson1First.entity.db.UserPassword;
import org.example.lesson1First.service.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.token.TokenService;
import org.springframework.web.bind.annotation.*;

import java.net.PasswordAuthentication;
import java.util.List;

@RestController
@RequestMapping("/api/jwt")
@RequiredArgsConstructor
public class JwtController {
    private final JwtService jwtService;

    private final SessionRegistry sessionRegistry;

    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @RequestHeader("Authorization") String authHeader,
            @CookieValue(value = "AUTH-TOKEN", required = false) String googleJwtToken
    ) {
        if (authHeader != null) {
            String jwtToken = authHeader.substring(7);
            jwtService.revokeToken(jwtToken);
        }
        if (googleJwtToken != null)
            jwtService.revokeToken(googleJwtToken);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout-all")
    public ResponseEntity<?> logoutAll(
            @AuthenticationPrincipal UserPassword user
    ) {
        jwtService.revokeAllUserTokens(user); // Отзываем все токены пользователя
        revokeAllSession(sessionRegistry, user);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/all")
    public ResponseEntity<?> getActiveSessions(@AuthenticationPrincipal UserPassword user) {
        List<TokenEntity> allTokens = jwtService.findAllAvailableUserToken(user);
        return ResponseEntity.ok(allTokens);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/admin/logout-all")
    public ResponseEntity<?> logoutAllAdminToUser(
            @RequestBody UserPassword user
    ) {
        jwtService.revokeAllUserTokens(user); // Отзываем все токены пользователя
        revokeAllSession(sessionRegistry, user);
        return ResponseEntity.ok().build();
    }

    public static void revokeAllSession(SessionRegistry sessionRegistry, UserPassword user){
        sessionRegistry.getAllSessions(user, false).forEach(SessionInformation::expireNow);
    }

}
