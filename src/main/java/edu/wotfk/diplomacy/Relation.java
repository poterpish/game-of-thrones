
package edu.wotfk.diplomacy;

public final class Relation {

    public enum Status {
        NEUTRAL,
        ALLIED,
        WAR
    }

    private Status status = Status.NEUTRAL;
    private int trust;
    private int truceUntil;

    public Relation(int trust) {
        this.trust = trust;
    }

    public Status status() {
        return status;
    }

    public void status(Status status) {
        this.status = status;
    }

    public int trust() {
        return trust;
    }

    public void changeTrust(int delta) {
        trust = Math.max(-100, Math.min(100, trust + delta));
    }

    public int truceUntil() {
        return truceUntil;
    }

    public void truceUntil(int round) {
        this.truceUntil = round;
    }
}
