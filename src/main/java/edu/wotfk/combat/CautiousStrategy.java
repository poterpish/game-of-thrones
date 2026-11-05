package edu.wotfk.combat;

public final class CautiousStrategy implements BattleStrategy {
    @Override public String name() { return "cautious"; }
    @Override public int attackPercent() { return 100; }
    @Override public int lossesWhenWinning(int troops) { return 0; }
    @Override public int lossesWhenLosing(int troops) { return 1; }
}
