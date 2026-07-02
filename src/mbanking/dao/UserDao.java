package mbanking.dao;

import mbanking.config.DatabaseConnection;
import mbanking.model.Account;
import mbanking.model.Transaction;
import mbanking.model.User;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class UserDao {
    private final TransactionDao transactionDao;

    public UserDao(TransactionDao transactionDao) {
        this.transactionDao = transactionDao;
    }

    public boolean usernameExists(String username) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean userIdExists(String userId) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public User findByUsername(String username) throws SQLException {
        String sql = baseUserQuery() + " WHERE u.username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapUser(conn, rs) : null;
            }
        }
    }

    public User findByAccountNumber(String accountNumber) throws SQLException {
        String sql = baseUserQuery() + " WHERE a.account_number = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, accountNumber);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapUser(conn, rs) : null;
            }
        }
    }

    public void insert(Connection conn, User user) throws SQLException {
        String insertUser = """
                INSERT INTO users (user_id, username, password, full_name)
                VALUES (?, ?, ?, ?)
                """;
        try (PreparedStatement ps = conn.prepareStatement(insertUser)) {
            ps.setString(1, user.getUserId());
            ps.setString(2, user.getUsername());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getFullName());
            ps.executeUpdate();
        }

        String insertAccount = """
                INSERT INTO accounts (account_number, user_id, balance, pin)
                VALUES (?, ?, ?, ?)
                """;
        try (PreparedStatement ps = conn.prepareStatement(insertAccount)) {
            ps.setString(1, user.getAccount().getAccountNumber());
            ps.setString(2, user.getUserId());
            ps.setBigDecimal(3, BigDecimal.valueOf(user.getAccount().getBalance()));
            ps.setString(4, user.getAccount().getPin());
            ps.executeUpdate();
        }
    }

    private String baseUserQuery() {
        return """
                SELECT u.user_id, u.username, u.password, u.full_name,
                       a.account_number, a.balance, a.pin
                FROM users u
                JOIN accounts a ON a.user_id = u.user_id
                """;
    }

    private User mapUser(Connection conn, ResultSet rs) throws SQLException {
        Account account = new Account(
                rs.getString("account_number"),
                rs.getBigDecimal("balance").doubleValue(),
                rs.getString("pin")
        );
        User user = new User(
                rs.getString("user_id"),
                rs.getString("username"),
                rs.getString("password"),
                rs.getString("full_name"),
                account
        );
        List<Transaction> transactions = transactionDao.findByAccountNumber(conn, account.getAccountNumber());
        for (Transaction transaction : transactions) {
            user.addTransaction(transaction);
        }
        return user;
    }
}
