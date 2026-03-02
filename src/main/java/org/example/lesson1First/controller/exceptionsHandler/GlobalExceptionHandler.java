package org.example.lesson1First.controller.exceptionsHandler;

import org.example.lesson1First.exception.UserAccountIsBlockingException;
import org.example.lesson1First.exception.superClasses.NotFoundException;
import org.example.lesson1First.exception.superClasses.UserInputException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, List<String>>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, List<String>> errors = new HashMap<>();
        Map<String, List<FieldError>> collect = ex.getBindingResult().getFieldErrors().stream().collect(Collectors.groupingBy(FieldError::getField));
        collect.keySet().forEach(it -> {
            List<String> mappedList = collect.get(it).stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toList();
            errors.put(it, mappedList);
        });

        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler
    public ResponseEntity<?> handleInputException(UserInputException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<?> handleNotFoundException(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<?> handleBlockingException(UserAccountIsBlockingException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", ex.getMessage()));
    }

}
