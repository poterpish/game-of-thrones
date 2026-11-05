package edu.wotfk.combat;

import java.util.Random;
/** Pure combat calculation apart from the explicitly supplied random stream. */
public final class BattleResolver {
    public BattleOutcome resolve(int attack, int defense, int morale, int attackingTroops, int defendingTroops, BattleStrategy strategy, Random random) {
        if (attack < 1 || defense < 0 || attackingTroops < 1 || defendingTroops < 0) throw new IllegalArgumentException("Invalid combatants.");
        int roll = 95 + random.nextInt(11);
        int score = Math.max(1, (int)((long)attack * (50 + morale) * strategy.attackPercent() * roll / 1_000_000L));
        boolean won = score > defense; // A tie favors the defender.
        int attackersLost = Math.min(attackingTroops, won ? strategy.lossesWhenWinning(attackingTroops) : strategy.lossesWhenLosing(attackingTroops));
        int defendersLost = Math.min(defendingTroops, won ? Math.max(1, (defendingTroops + 1) / 2) : (score * 2 >= defense ? 1 : 0));
        return new BattleOutcome(won, attackersLost, defendersLost, score, defense, strategy.name());
    }
}
