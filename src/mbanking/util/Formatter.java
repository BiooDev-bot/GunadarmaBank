package mbanking.util;

public final class Formatter {
    private Formatter() {
    }

    public static String formatCurrency(double amount) {
        return String.format("Rp%,.0f", amount);
    }
}
