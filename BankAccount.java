package BankSystem;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class BankAccount {

    protected final String accountNumber;
    protected final Customer customer;
    protected BigDecimal balance;
    protected final List<Transaction> transactions = new ArrayList<>();

    public BankAccount(String accountNumber, Customer customer, BigDecimal initialBalance) {
        this.accountNumber = accountNumber;
        this.customer = customer;
        this.balance = initialBalance == null ? BigDecimal.ZERO : initialBalance;
    }

    public String getAccountNumber() { return accountNumber; }
    public Customer getCustomer() { return customer; }
    public BigDecimal getBalance() { return balance; }

    public void deposit(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("Amount must be positive");
        balance = balance.add(amount);
        transactions.add(new Transaction("DEPOSIT", amount, LocalDateTime.now()));
    }

    /**
     * Attempt to withdraw. Returns true if successful, false otherwise.
     */
    public abstract boolean withdraw(BigDecimal amount);

    public List<Transaction> getTransactions() { return transactions; }
}