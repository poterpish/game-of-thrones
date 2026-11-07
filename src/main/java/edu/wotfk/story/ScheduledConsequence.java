package edu.wotfk.story;

import edu.wotfk.domain.HouseId;
/** Serializable-style data, rather than a stored arbitrary callback. */
public record ScheduledConsequence(int id,int dueRound,HouseId house,Kind kind,int amount,String reason) {
    public enum Kind { DEBT, FOOD, GOLD, MORALE, TRUST, LEVY_REACTION, BACKLASH }
    public ScheduledConsequence { if(id<1||dueRound<1||amount<0)throw new IllegalArgumentException("Invalid scheduled consequence."); java.util.Objects.requireNonNull(house);java.util.Objects.requireNonNull(kind);java.util.Objects.requireNonNull(reason); }
}
