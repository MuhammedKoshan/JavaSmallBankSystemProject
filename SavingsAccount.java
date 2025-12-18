package BankSystem;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class SavingsAccount extends BankAccount {

    private static final BigDecimal MIN_BALANCE = BigDecimal.ZERO; // simple rule

    public SavingsAccount(String accountNumber, Customer customer, BigDecimal initialBalance) {
        super(accountNumber, customer, initialBalance);
    }

    @Override
    public boolean withdraw(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) return false;
        BigDecimal newBal = balance.subtract(amount);
        if (newBal.compareTo(MIN_BALANCE) < 0) {
            transactions.add(new Transaction("WITHDRAW_FAILED", amount, LocalDateTime.now()));
            return false;
        }
        balance = newBal;
        transactions.add(new Transaction("WITHDRAW", amount, LocalDateTime.now()));
        return true;
    }
    
}
