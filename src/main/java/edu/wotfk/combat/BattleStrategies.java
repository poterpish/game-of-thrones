package edu.wotfk.combat;

import java.util.Locale;
public final class BattleStrategies {
    private BattleStrategies() { }
    public static BattleStrategy named(String name) {
        return switch (name.toLowerCase(Locale.ROOT)) {
            case "assault" -> new AssaultStrategy();
            case "cautious" -> new CautiousStrategy();
            default -> throw new IllegalArgumentException("Tactic must be assault or cautious.");
        };
    }
}
