package org.example.lesson1First.config.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.example.lesson1First.service.JwtService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@Component
@Log4j2
@RequiredArgsConstructor
public class OAuthCookieFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final SecurityContextRepository securityContextRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {


        log.info("OAuthCookieFilter START authentication");

        String token = extractToken(request);

        if (SecurityContextHolder.getContext().getAuthentication() instanceof OAuth2AuthenticationToken){
            cleaerContextAndSession(request);
        }

        if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            if (jwtService.isTokenValid(token)) {
                SecurityContext context = SecurityContextHolder.createEmptyContext();
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(jwtService.extractEmail(token));

                // Создаем аутентифицированный объект
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                context.setAuthentication(auth);

                SecurityContextHolder.setContext(context);
                securityContextRepository.saveContext(context, request, response);
            }
        }
        log.info("OAuthCookieFilter FINISH authentication");
        filterChain.doFilter(request, response);
    }

    private static void cleaerContextAndSession(HttpServletRequest request) {
        SecurityContextHolder.clearContext();
        HttpSession session = request.getSession(false);
            if (session != null) {
                Object springSecurityContext = session.getAttribute("SPRING_SECURITY_CONTEXT");
                if (springSecurityContext instanceof SecurityContext secContext) {
                    secContext.setAuthentication(null);
                }
        }
    }

    private String extractToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;
        Optional<Cookie> cookie = Arrays.stream(cookies).filter(i -> i.getName().equals("AUTH-TOKEN")).findFirst();
        return cookie.map(Cookie::getValue).orElse(null);
    }
}
