package mbanking.service;

import mbanking.dao.DatabaseException;
import mbanking.enums.TransactionType;
import mbanking.model.Account;
import mbanking.model.Transaction;
import mbanking.model.User;
import mbanking.repository.BankRepository;
import mbanking.util.IDGenerator;

import java.sql.SQLException;

public class Bank {
    private final String bankName;
    private final BankRepository repository;

    public Bank(String bankName) {
        this.bankName = bankName;
        this.repository = new BankRepository();
        seedDemoAccounts();
    }

    private void seedDemoAccounts() {
        try {
            if (!repository.usernameExists("budi")) {
                registerUser("Budi Santoso", "budi", "budi123", "123456", 5_000_000);
            }
            if (!repository.usernameExists("siti")) {
                registerUser("Siti Rahayu", "siti", "siti123", "654321", 3_000_000);
            }
        } catch (SQLException | IllegalStateException e) {
            throw new DatabaseException("Gagal menginisialisasi data demo Gunadarma Bank.", e);
        }
    }

    public String registerUser(String fullName, String username, String password,
                               String pin, double initialBalance) {
        try {
            String normalizedUsername = username.toLowerCase();
            if (repository.usernameExists(normalizedUsername)) {
                return null;
            }

            String userId = generateUniqueUserId();
            String accountNumber = generateUniqueAccountNumber();
            Account account = new Account(accountNumber, initialBalance, pin);
            User user = new User(userId, normalizedUsername, password, fullName, account);

            repository.createUser(user);
            return accountNumber;
        } catch (SQLException | IllegalStateException e) {
            throw new DatabaseException("Gagal menyimpan data pengguna ke database.", e);
        }
    }

    public User login(String username, String password) {
        try {
            User user = repository.findByUsername(username.toLowerCase());
            if (user != null && user.validatePassword(password)) {
                return user;
            }
            return null;
        } catch (SQLException | IllegalStateException e) {
            throw new DatabaseException("Gagal membaca data login dari database.", e);
        }
    }

    public boolean deposit(User user, double amount, String pin) {
        if (!user.getAccount().validatePin(pin)) return false;
        double oldBalance = user.getAccount().getBalance();
        if (!user.getAccount().deposit(amount)) return false;

        Transaction tx = new Transaction(
                generateUniqueTransactionId(),
                TransactionType.DEPOSIT,
                amount,
                user.getAccount().getAccountNumber(),
                user.getAccount().getAccountNumber(),
                "Setor Tunai"
        );

        try {
            repository.saveDeposit(user, tx);
            user.addTransaction(tx);
            return true;
        } catch (SQLException | IllegalStateException e) {
            user.getAccount().setBalance(oldBalance);
            throw new DatabaseException("Gagal menyimpan transaksi setor tunai.", e);
        }
    }

    public String withdraw(User user, double amount, String pin) {
        if (!user.getAccount().validatePin(pin)) return "PIN salah.";
        if (amount <= 0) return "Nominal tidak valid.";
        if (amount > user.getAccount().getBalance()) return "Saldo tidak mencukupi.";

        double oldBalance = user.getAccount().getBalance();
        user.getAccount().withdraw(amount);

        Transaction tx = new Transaction(
                generateUniqueTransactionId(),
                TransactionType.WITHDRAW,
                amount,
                user.getAccount().getAccountNumber(),
                "-",
                "Tarik Tunai"
        );

        try {
            repository.saveWithdrawal(user, tx);
            user.addTransaction(tx);
            return "OK";
        } catch (SQLException | IllegalStateException e) {
            user.getAccount().setBalance(oldBalance);
            throw new DatabaseException("Gagal menyimpan transaksi tarik tunai.", e);
        }
    }

    public String transfer(User sender, String targetAccountNumber, double amount, String pin) {
        if (!sender.getAccount().validatePin(pin)) return "PIN salah.";
        if (amount <= 0) return "Nominal tidak valid.";
        if (targetAccountNumber.equals(sender.getAccount().getAccountNumber()))
            return "Tidak bisa transfer ke rekening sendiri.";

        User receiver = findUserByAccount(targetAccountNumber);
        if (receiver == null) return "Nomor rekening tujuan tidak ditemukan.";
        if (amount > sender.getAccount().getBalance()) return "Saldo tidak mencukupi.";

        double oldSenderBalance = sender.getAccount().getBalance();
        double oldReceiverBalance = receiver.getAccount().getBalance();

        sender.getAccount().withdraw(amount);
        Transaction txOut = new Transaction(
                generateUniqueTransactionId(),
                TransactionType.TRANSFER_OUT,
                amount,
                sender.getAccount().getAccountNumber(),
                targetAccountNumber,
                "Transfer ke " + receiver.getFullName()
        );

        receiver.getAccount().deposit(amount);
        Transaction txIn = new Transaction(
                generateUniqueTransactionId(),
                TransactionType.TRANSFER_IN,
                amount,
                sender.getAccount().getAccountNumber(),
                targetAccountNumber,
                "Transfer dari " + sender.getFullName()
        );

        try {
            repository.saveTransfer(sender, receiver, txOut, txIn);
            sender.addTransaction(txOut);
            receiver.addTransaction(txIn);
            return "OK:" + receiver.getFullName();
        } catch (SQLException | IllegalStateException e) {
            sender.getAccount().setBalance(oldSenderBalance);
            receiver.getAccount().setBalance(oldReceiverBalance);
            throw new DatabaseException("Gagal menyimpan transaksi transfer.", e);
        }
    }

    public String changePin(User user, String oldPin, String newPin) {
        if (!user.getAccount().validatePin(oldPin)) return "PIN lama salah.";
        if (newPin.length() != 6 || !newPin.matches("\\d+")) return "PIN baru harus 6 digit angka.";

        String previousPin = user.getAccount().getPin();
        user.getAccount().setPin(newPin);
        try {
            repository.savePinChange(user);
            return "OK";
        } catch (SQLException | IllegalStateException e) {
            user.getAccount().setPin(previousPin);
            throw new DatabaseException("Gagal menyimpan perubahan PIN.", e);
        }
    }

    public User findUserByAccount(String accountNumber) {
        try {
            return repository.findByAccountNumber(accountNumber);
        } catch (SQLException | IllegalStateException e) {
            throw new DatabaseException("Gagal membaca data rekening dari database.", e);
        }
    }

    public String getBankName() { return bankName; }

    private String generateUniqueUserId() throws SQLException {
        String userId;
        do {
            userId = IDGenerator.generateUserId();
        } while (repository.userIdExists(userId));
        return userId;
    }

    private String generateUniqueAccountNumber() throws SQLException {
        String accountNumber;
        do {
            accountNumber = IDGenerator.generateAccountNumber();
        } while (repository.accountNumberExists(accountNumber));
        return accountNumber;
    }

    private String generateUniqueTransactionId() {
        try {
            String transactionId;
            do {
                transactionId = IDGenerator.generateTransactionId();
            } while (repository.transactionIdExists(transactionId));
            return transactionId;
        } catch (SQLException | IllegalStateException e) {
            throw new DatabaseException("Gagal membuat ID transaksi unik.", e);
        }
    }
}
