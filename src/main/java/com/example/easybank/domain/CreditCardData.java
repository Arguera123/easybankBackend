package com.example.easybank.domain;

import java.time.YearMonth;

public record CreditCardData(String cardNumber, String cvv, YearMonth expiration) {
    @Override
    public String toString() {
        return "Card: " + cardNumber + ", CVV: " + cvv + ", Exp: " + expiration;
    }
}

