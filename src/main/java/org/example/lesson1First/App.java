package org.example.lesson1First;

//import io.swagger.v3.oas.annotations.OpenAPIDefinition;
//import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Hello world!
 */
//@OpenAPIDefinition(
//        info = @Info(
//                title = "Banking API",
//                version = "1.0",
//                description = "API для управления банковскими счетами и транзакциями"
//        )
//)
@SpringBootApplication
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}