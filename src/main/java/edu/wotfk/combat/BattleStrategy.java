package edu.wotfk.combat;

/** Strategy changes offensive pressure and casualty policy, not ownership rules. */
public interface BattleStrategy {
    String name();
    int attackPercent();
    int lossesWhenWinning(int troops);
    int lossesWhenLosing(int troops);
}
