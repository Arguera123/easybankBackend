package com.example.easybank.util.generator;

import com.example.easybank.domain.CreditCardData;
import org.springframework.stereotype.Component;

import java.time.YearMonth;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class RandomCreditCardGenerator implements CreditCardGenerator {

    private final Random random = new Random();

    @Override
    public CreditCardData generate() {
        String cardNumber = generateValidCardNumber();
        String cvv = String.format("%03d", random.nextInt(1000));
        YearMonth expiration = YearMonth.now().plusMonths(random.nextInt(12) + 1);
        return new CreditCardData(cardNumber, cvv, expiration);
    }

    private String generateValidCardNumber() {
        int[] digits = new int[16];
        for (int i = 0; i < 15; i++) {
            digits[i] = random.nextInt(10);
        }

        // Luhn checksum
        int sum = 0;
        for (int i = 0; i < 15; i++) {
            int digit = digits[14 - i];
            if (i % 2 == 0) digit *= 2;
            if (digit > 9) digit -= 9;
            sum += digit;
        }
        digits[15] = (10 - (sum % 10)) % 10;

        return IntStream.of(digits)
                .mapToObj(String::valueOf)
                .collect(Collectors.joining());
    }
}

