package edu.wotfk.economy;

/** A calculation result, not a payment. Re-reading a report cannot apply it twice. */
public record EconomyLedger(int goldIncome, int foodIncome, int goldUpkeep, int foodUpkeep) {
    public String summary() {
        return "Income: " + goldIncome + " gold, " + foodIncome + " food. Upkeep: " + goldUpkeep + " gold, " + foodUpkeep + " food.";
    }
}
