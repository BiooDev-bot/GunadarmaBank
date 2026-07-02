package mbanking.util;

import java.util.concurrent.atomic.AtomicInteger;

public class IDGenerator {
    private static final AtomicInteger userCounter = new AtomicInteger(1);
    private static final AtomicInteger txCounter = new AtomicInteger(1);
    private static final AtomicInteger accCounter = new AtomicInteger(10000001);

    public static String generateUserId() {
        return String.format("USR%04d", userCounter.getAndIncrement());
    }

    public static String generateAccountNumber() {
        return String.format("%08d", accCounter.getAndIncrement());
    }

    public static String generateTransactionId() {
        return String.format("TXN%08d", txCounter.getAndIncrement());
    }
}
