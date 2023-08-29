package com.toyproject.authsystem;

public class IncorrectPasswordException extends RuntimeException {
    public IncorrectPasswordException() {
        super("현재 비밀번호가 틀립니다");
    }
}
