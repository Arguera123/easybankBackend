package com.example.easybank.exception;

public class RecipientNameMismatchException extends RuntimeException {
    public RecipientNameMismatchException(String message) {
        super(message);
    }
}
