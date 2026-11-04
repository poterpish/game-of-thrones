package edu.wotfk.domain;

/** Bounded, non-negative resource balances. A rejected payment changes nothing. */
public final class Resources {
    private int gold;
    private int food;
    private int influence;
    public Resources(int gold, int food, int influence) {
        if (gold < 0 || food < 0 || influence < 0) throw new IllegalArgumentException("Negative initial resource.");
        this.gold = Math.min(999, gold); this.food = Math.min(999, food); this.influence = Math.min(99, influence);
    }
    public int gold() { return gold; }
    public int food() { return food; }
    public int influence() { return influence; }
    public boolean canPay(Cost cost) { return gold >= cost.gold() && food >= cost.food() && influence >= cost.influence(); }
    public void pay(Cost cost) {
        if (!canPay(cost)) throw new IllegalStateException("Not enough resources.");
        gold -= cost.gold(); food -= cost.food(); influence -= cost.influence();
    }
    public void gain(int gold, int food, int influence) {
        if (gold < 0 || food < 0 || influence < 0) throw new IllegalArgumentException("Use pay() for spending.");
        this.gold = (int) Math.min(999L, (long)this.gold + gold);
        this.food = (int) Math.min(999L, (long)this.food + food);
        this.influence = (int) Math.min(99L, (long)this.influence + influence);
    }
    public String summary() { return "Gold " + gold + "   Food " + food + "   Influence " + influence; }
}
