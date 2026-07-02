package mbanking.model;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String userId;
    private String username;
    private String password;
    private String fullName;
    private Account account;
    private List<Transaction> transactions;

    public User(String userId, String username, String password,
                String fullName, Account account) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.account = account;
        this.transactions = new ArrayList<>();
    }

    // ── Getters & Setters ──────────────────────────────────────────
    public String getUserId() { return userId; }

    public String getUsername() { return username; }

    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }

    public String getFullName() { return fullName; }

    public Account getAccount() { return account; }

    public List<Transaction> getTransactions() { return transactions; }

    // ── Methods ────────────────────────────────────────────────────
    public void addTransaction(Transaction t) {
        transactions.add(t);
    }

    public boolean validatePassword(String inputPassword) {
        return this.password.equals(inputPassword);
    }
}
