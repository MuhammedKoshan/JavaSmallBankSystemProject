package BankSystem;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CheckingAccount extends BankAccount {

    private static final BigDecimal OVERDRAFT_LIMIT = new BigDecimal("200"); // can go negative up to -200

    public CheckingAccount(String accountNumber, Customer customer, BigDecimal initialBalance) {
        super(accountNumber, customer, initialBalance);
    }

    @Override
    public boolean withdraw(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) return false;
        BigDecimal allowed = balance.add(OVERDRAFT_LIMIT);
        if (allowed.compareTo(amount) < 0) {
            transactions.add(new Transaction("WITHDRAW_FAILED", amount, LocalDateTime.now()));
            return false;
        }
        balance = balance.subtract(amount);
        transactions.add(new Transaction("WITHDRAW", amount, LocalDateTime.now()));
        return true;
    }
}