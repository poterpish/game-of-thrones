package edu.wotfk.testsupport;

import java.util.Objects;
public final class TestSupport {
    private TestSupport() { }
    public static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
        System.out.println("[PASS] " + message);
    }
    public static void equal(Object expected, Object actual, String message) {
        check(Objects.equals(expected, actual), message + " (expected " + expected + ", got " + actual + ")");
    }
    public static void rejects(Runnable operation, String message) {
        try { operation.run(); } catch (IllegalArgumentException | IllegalStateException expected) {
            System.out.println("[PASS] " + message); return;
        }
        throw new AssertionError(message);
    }
}
