package mbanking.model;

public class Account {
    private String accountNumber;
    private double balance;
    private String pin;

    public Account(String accountNumber, double initialBalance, String pin) {
        this.accountNumber = accountNumber;
        this.balance = initialBalance;
        this.pin = pin;
    }

    // ── Getters & Setters ──────────────────────────────────────────
    public String getAccountNumber() { return accountNumber; }

    public double getBalance() { return balance; }

    public void setBalance(double balance) { this.balance = balance; }

    public String getPin() { return pin; }

    public void setPin(String pin) { this.pin = pin; }

    // ── Business Methods ───────────────────────────────────────────
    public boolean deposit(double amount) {
        if (amount <= 0) return false;
        this.balance += amount;
        return true;
    }

    public boolean withdraw(double amount) {
        if (amount <= 0 || amount > this.balance) return false;
        this.balance -= amount;
        return true;
    }

    public boolean validatePin(String inputPin) {
        return this.pin.equals(inputPin);
    }
}
