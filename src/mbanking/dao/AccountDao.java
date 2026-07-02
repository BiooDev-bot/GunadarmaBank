package mbanking.dao;

import mbanking.config.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountDao {
    public boolean accountNumberExists(String accountNumber) throws SQLException {
        String sql = "SELECT 1 FROM accounts WHERE account_number = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, accountNumber);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public void updateBalance(Connection conn, String accountNumber, double balance) throws SQLException {
        String sql = "UPDATE accounts SET balance = ? WHERE account_number = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBigDecimal(1, BigDecimal.valueOf(balance));
            ps.setString(2, accountNumber);
            ps.executeUpdate();
        }
    }

    public void updatePin(Connection conn, String accountNumber, String pin) throws SQLException {
        String sql = "UPDATE accounts SET pin = ? WHERE account_number = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pin);
            ps.setString(2, accountNumber);
            ps.executeUpdate();
        }
    }
}
