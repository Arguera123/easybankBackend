package com.example.easybank.exception;

public class EmptyCardListException extends RuntimeException {
    public EmptyCardListException(String message) {
        super(message);
    }
}
