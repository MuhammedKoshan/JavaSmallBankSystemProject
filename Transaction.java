package BankSystem;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaction {

    private final String type;
    private final BigDecimal amount;
    private final LocalDateTime timestamp;

    public Transaction(String type, BigDecimal amount, LocalDateTime timestamp) {
        this.type = type;
        this.amount = amount;
        this.timestamp = timestamp == null ? LocalDateTime.now() : timestamp;
    }

    public String getType() { return type; }
    public BigDecimal getAmount() { return amount; }
    public LocalDateTime getDate() { return timestamp; }

    @Override
    public String toString() {
        return timestamp + " | " + type + " | " + amount;
    }
}