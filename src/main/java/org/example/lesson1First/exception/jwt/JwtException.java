package org.example.lesson1First.exception.jwt;

import org.example.lesson1First.exception.superClasses.InvalidRequestParamException;

public class JwtException extends InvalidRequestParamException {
    public JwtException(String message) {
        super(message);
    }
}
