package org.example.lesson1First.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class LoginHtmlController {


    @GetMapping("/login/google")
    public String loginPage(Model model) {
        // Можно передать список доступных провайдеров
        model.addAttribute("providers", List.of("google", "github", "facebook"));
        return "login-oauth";
    }

    // 1. Показываем форму логина
    @GetMapping("/login/form")
    public String showLoginForm(
            @RequestParam(required = false) String error,
            @RequestParam(required = false) String logout,
            Model model) {

        if (error != null) {
            model.addAttribute("errorMessage", "❌ Неверный логин или пароль!");
        }

        if (logout != null) {
            model.addAttribute("successMessage", "✅ Вы успешно вышли!");
        }

        return "login-page"; // Имя HTML файла в templates/
    }

    // 2. Страница после успешного логина
    @GetMapping("/home")
    public String homePage() {
        return "home-page";
    }
}

