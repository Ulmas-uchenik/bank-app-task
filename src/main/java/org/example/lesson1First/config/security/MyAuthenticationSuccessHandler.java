package org.example.lesson1First.config.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.example.lesson1First.entity.db.UserPassword;
import org.example.lesson1First.repository.UserPasswordRepository;
import org.example.lesson1First.service.JwtService;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Log4j2
@Component
@RequiredArgsConstructor
public class MyAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final UserPasswordRepository userPasswordRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OidcUser oidcUser = (OidcUser) authentication.getPrincipal();
        String email = oidcUser.getEmail();

        UserPassword userDetails = new UserPassword(email, "ROLE_USER", null);
        String myToken = jwtService.generateToken(userDetails);
        userPasswordRepository.save(userDetails);

//             2. Отдаем его клиенту (в куках или через redirect с параметром)
        Cookie jwtCookie = new Cookie("AUTH-TOKEN", myToken);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setPath("/");
        response.addCookie(jwtCookie);

        // 3. Редиректим на фронтенд
        getRedirectStrategy().sendRedirect(request, response, "/");
    }
}

