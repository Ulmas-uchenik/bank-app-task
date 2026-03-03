package org.example.lesson1First.config;

import org.example.lesson1First.config.security.JwtAuthFilter;
import org.example.lesson1First.config.security.MyAuthenticationSuccessHandler;
import org.example.lesson1First.config.security.OAuthCookieFilter;
import org.example.lesson1First.service.MyUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.DelegatingSecurityContextRepository;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextHolderFilter;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.session.HttpSessionEventPublisher;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {

    private final AuthenticationEntryPoint entryPointFormAuthentication = (request, response, authException) -> {
        String requestURI = request.getRequestURI();
        if (requestURI.contains("form") && !requestURI.contains("login")) {
            response.sendRedirect("/login/form");
        } else if (requestURI.contains("basic")) {
            response.addHeader("WWW-Authenticate", "Basic realm=\"My App\"");
        }
        response.setStatus(401);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"Please authenticate on url \"/basic\" or \"/form\"\"}");
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthFilter jwtAuthFilter, MyAuthenticationSuccessHandler myAuthenticationSuccessHandler) {
        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        http
                .sessionManagement(session -> session
                        .maximumSessions(2) // Макс. число сессий на пользователя
                        .sessionRegistry(sessionRegistry())
                        .maxSessionsPreventsLogin(true) // Блокировать новые сессии при превышении
                )
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth.
                        requestMatchers("/public/**", "/login/**", "/error").permitAll()
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults())
                .formLogin(form -> form.loginPage("/login/form")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/", true)
                        .failureUrl("/login/form?error=error")
                        .permitAll()
                )

                .logout(logout -> logout.logoutUrl("/login/logout")
                        .logoutSuccessUrl("/login/form?logout=logout") // После выхода
                        .permitAll())
                .exceptionHandling(handling -> handling
                        .authenticationEntryPoint(entryPointFormAuthentication));
        return http.build();
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new DelegatingSecurityContextRepository(
                new RequestAttributeSecurityContextRepository()
        );
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService());
        provider.setPasswordEncoder(passwordEncoder());

        return provider;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new MyUserDetailsService();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> customOAuth2UserService() {
        return new DefaultOAuth2UserService() {
            @Override
            public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
                OAuth2User user = super.loadUser(userRequest);

                // Можем модифицировать или дополнить данные пользователя
                Map<String, Object> attributes = new HashMap<>(user.getAttributes());
                // Добавляем дополнительные поля
                attributes.put("login_method", "oauth2");
                attributes.put("provider", userRequest.getClientRegistration().getRegistrationId());

                DefaultOAuth2User defaultOAuth2User = new DefaultOAuth2User(
                        user.getAuthorities(),
                        attributes,
                        userRequest.getClientRegistration()
                                .getProviderDetails()
                                .getUserInfoEndpoint()
                                .getUserNameAttributeName()
                );
                return defaultOAuth2User;
            }
        };
    }

}