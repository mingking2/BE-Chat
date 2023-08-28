package com.toyproject.authsystem.service;

// StatusAlreadyExistsException 클래스 정의
public class StatusAlreadyExistsException extends RuntimeException {
    public StatusAlreadyExistsException(String message) {
        super(message);
    }
}
