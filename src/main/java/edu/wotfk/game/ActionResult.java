
package edu.wotfk.game;

/** Result of a player command. */
public record ActionResult(boolean success, String message) {

    public boolean accepted() {
        return success;
    }

    @Override
    public String toString() {
        return (success ? "OK: " : "Not allowed: ") + message;
    }
}
