package edu.wotfk.game;
import edu.wotfk.campaign.CampaignState;
import edu.wotfk.domain.*;
/** Shared validation and atomic payment for game actions. */
public final class GameRules {
    private GameRules() { }
    public static void require(boolean condition, String message) {
        if (!condition) throw new IllegalStateException(message);
    }
    public static void ready(CampaignState state, HouseId actor, Cost cost, int actions) {
        require(!state.finished(), "The campaign is finished.");
        HouseState house = state.house(actor);
        require(state.alive(actor), "This house has no territory.");
        require(actions >= 0 && house.actions() >= actions, "Not enough action points.");
        require(house.resources().canPay(cost), "Not enough resources.");
    }
    public static void pay(CampaignState state, HouseId actor, Cost cost, int actions) {
        ready(state, actor, cost, actions);
        HouseState house = state.house(actor);
        house.resources().pay(cost);
        house.spendActions(actions);
    }
}
