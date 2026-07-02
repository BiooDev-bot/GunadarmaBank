package mbanking.dao;

import mbanking.config.DatabaseConnection;
import mbanking.enums.TransactionType;
import mbanking.model.Transaction;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class TransactionDao {
    public boolean transactionIdExists(String transactionId) throws SQLException {
        String sql = "SELECT 1 FROM transactions WHERE transaction_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, transactionId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public List<Transaction> findByAccountNumber(Connection conn, String accountNumber) throws SQLException {
        String sql = """
                SELECT transaction_id, transaction_type, amount, from_account_number,
                       to_account_number, description, created_at
                FROM transactions
                WHERE account_number = ?
                ORDER BY created_at ASC, transaction_id ASC
                """;
        List<Transaction> transactions = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, accountNumber);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String fromAccount = rs.getString("from_account_number");
                    String toAccount = rs.getString("to_account_number");
                    Timestamp createdAt = rs.getTimestamp("created_at");

                    transactions.add(new Transaction(
                            rs.getString("transaction_id"),
                            TransactionType.valueOf(rs.getString("transaction_type")),
                            rs.getBigDecimal("amount").doubleValue(),
                            fromAccount == null ? "-" : fromAccount,
                            toAccount == null ? "-" : toAccount,
                            rs.getString("description"),
                            createdAt.toLocalDateTime()
                    ));
                }
            }
        }
        return transactions;
    }

    public void insert(Connection conn, Transaction transaction) throws SQLException {
        String sql = """
                INSERT INTO transactions (
                    transaction_id, account_number, transaction_type, amount,
                    from_account_number, to_account_number, description, created_at
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, transaction.getTransactionId());
            ps.setString(2, getOwnerAccountNumber(transaction));
            ps.setString(3, transaction.getType().name());
            ps.setBigDecimal(4, BigDecimal.valueOf(transaction.getAmount()));
            ps.setString(5, normalizeAccountNumber(transaction.getFromAccount()));
            ps.setString(6, normalizeAccountNumber(transaction.getToAccount()));
            ps.setString(7, transaction.getDescription());
            ps.setTimestamp(8, Timestamp.valueOf(transaction.getTimestamp()));
            ps.executeUpdate();
        }
    }

    private String getOwnerAccountNumber(Transaction transaction) {
        if (transaction.getType() == TransactionType.TRANSFER_IN) {
            return transaction.getToAccount();
        }
        return transaction.getFromAccount();
    }

    private String normalizeAccountNumber(String accountNumber) {
        if (accountNumber == null || "-".equals(accountNumber)) {
            return null;
        }
        return accountNumber;
    }
}
