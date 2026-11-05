package edu.wotfk.combat;

public final class AssaultStrategy implements BattleStrategy {
    @Override public String name() { return "assault"; }
    @Override public int attackPercent() { return 125; }
    @Override public int lossesWhenWinning(int troops) { return Math.min(1, Math.max(0, troops - 1)); }
    @Override public int lossesWhenLosing(int troops) { return Math.max(1, (troops + 1) / 2); }
}
