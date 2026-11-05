package edu.wotfk.combat;

public record BattleOutcome(boolean victory, int attackerLosses, int defenderLosses, int attackScore, int defenseScore, String tactic) {
    public String summary() {
        return (victory ? "VICTORY" : "DEFEAT") + " using " + tactic + ": power " + attackScore + " vs " + defenseScore
                + "; your losses " + attackerLosses + ", defending army losses " + defenderLosses + ".";
    }
}
