package mbanking.model;

import mbanking.enums.TransactionType;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaction {
    private String transactionId;
    private TransactionType type;
    private double amount;
    private LocalDateTime timestamp;
    private String description;
    private String fromAccount;
    private String toAccount;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public Transaction(String transactionId, TransactionType type, double amount,
                       String fromAccount, String toAccount, String description) {
        this(transactionId, type, amount, fromAccount, toAccount, description, LocalDateTime.now());
    }

    public Transaction(String transactionId, TransactionType type, double amount,
                       String fromAccount, String toAccount, String description,
                       LocalDateTime timestamp) {
        this.transactionId = transactionId;
        this.type = type;
        this.amount = amount;
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.description = description;
        this.timestamp = timestamp;
    }

    public String getTransactionId() { return transactionId; }
    public TransactionType getType() { return type; }
    public double getAmount() { return amount; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getDescription() { return description; }
    public String getFromAccount() { return fromAccount; }
    public String getToAccount() { return toAccount; }

    public String getFormattedTimestamp() {
        return timestamp.format(FORMATTER);
    }

    @Override
    public String toString() {
        String sign = (type == TransactionType.DEPOSIT || type == TransactionType.TRANSFER_IN)
                ? "+" : "-";
        return String.format("  [%s] %-18s %s Rp%,.0f",
                getFormattedTimestamp(),
                type.getLabel(),
                sign,
                amount);
    }
}
