package com.example.easybank.exception;

import org.springframework.http.HttpStatus;

import java.time.LocalDate;

public record CustomErrorResponse (
        LocalDate timestamp,
        String message,
        String path
){
}