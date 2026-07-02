package mbanking.repository;

import mbanking.config.DatabaseConnection;
import mbanking.dao.AccountDao;
import mbanking.dao.TransactionDao;
import mbanking.dao.UserDao;
import mbanking.model.Transaction;
import mbanking.model.User;

import java.sql.Connection;
import java.sql.SQLException;

public class BankRepository {
    private final TransactionDao transactionDao;
    private final UserDao userDao;
    private final AccountDao accountDao;

    public BankRepository() {
        this.transactionDao = new TransactionDao();
        this.userDao = new UserDao(transactionDao);
        this.accountDao = new AccountDao();
    }

    public boolean usernameExists(String username) throws SQLException {
        return userDao.usernameExists(username);
    }

    public boolean userIdExists(String userId) throws SQLException {
        return userDao.userIdExists(userId);
    }

    public boolean accountNumberExists(String accountNumber) throws SQLException {
        return accountDao.accountNumberExists(accountNumber);
    }

    public boolean transactionIdExists(String transactionId) throws SQLException {
        return transactionDao.transactionIdExists(transactionId);
    }

    public User findByUsername(String username) throws SQLException {
        return userDao.findByUsername(username);
    }

    public User findByAccountNumber(String accountNumber) throws SQLException {
        return userDao.findByAccountNumber(accountNumber);
    }

    public void createUser(User user) throws SQLException {
        executeInTransaction(conn -> userDao.insert(conn, user));
    }

    public void saveDeposit(User user, Transaction transaction) throws SQLException {
        executeInTransaction(conn -> {
            accountDao.updateBalance(conn, user.getAccount().getAccountNumber(), user.getAccount().getBalance());
            transactionDao.insert(conn, transaction);
        });
    }

    public void saveWithdrawal(User user, Transaction transaction) throws SQLException {
        executeInTransaction(conn -> {
            accountDao.updateBalance(conn, user.getAccount().getAccountNumber(), user.getAccount().getBalance());
            transactionDao.insert(conn, transaction);
        });
    }

    public void saveTransfer(User sender, User receiver, Transaction txOut, Transaction txIn) throws SQLException {
        executeInTransaction(conn -> {
            accountDao.updateBalance(conn, sender.getAccount().getAccountNumber(), sender.getAccount().getBalance());
            accountDao.updateBalance(conn, receiver.getAccount().getAccountNumber(), receiver.getAccount().getBalance());
            transactionDao.insert(conn, txOut);
            transactionDao.insert(conn, txIn);
        });
    }

    public void savePinChange(User user) throws SQLException {
        executeInTransaction(conn ->
                accountDao.updatePin(conn, user.getAccount().getAccountNumber(), user.getAccount().getPin())
        );
    }

    private void executeInTransaction(SqlWork work) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            boolean originalAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
            try {
                work.execute(conn);
                conn.commit();
            } catch (SQLException | RuntimeException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(originalAutoCommit);
            }
        }
    }

    @FunctionalInterface
    private interface SqlWork {
        void execute(Connection conn) throws SQLException;
    }
}
