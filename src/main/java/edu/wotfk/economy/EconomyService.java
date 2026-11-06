package edu.wotfk.economy;

import edu.wotfk.campaign.CampaignState;
import edu.wotfk.domain.*;
public final class EconomyService {
    public EconomyLedger forecast(CampaignState state, HouseId owner) {
        EconomyVisitor visitor = new EconomyVisitor(owner, state.round(), state.round() >= 7)
                .excluding(state.sieges().values().stream().map(edu.wotfk.combat.Siege::target).collect(java.util.stream.Collectors.toSet()));
        for (Territory territory : state.map().territories()) territory.accept(visitor);
        for (HouseState house : state.houses()) house.army().accept(visitor);
        EconomyLedger ledger = visitor.ledger();
        int trade = (int) java.util.Arrays.stream(HouseId.values()).filter(other -> other != owner && state.alive(other) && state.diplomacy().allied(owner,other)).count();
        return new EconomyLedger(ledger.goldIncome()+trade, ledger.foodIncome(), ledger.goldUpkeep(), ledger.foodUpkeep());
    }
    /** Only the turn coordinator calls settlement, once per completed round. */
    public EconomyLedger settle(CampaignState state, HouseId owner) {
        HouseState house = state.house(owner); EconomyLedger ledger = forecast(state, owner);
        Resources wallet = house.resources(); wallet.gain(ledger.goldIncome(), ledger.foodIncome(), 1);
        boolean unpaid = wallet.gold() < ledger.goldUpkeep(); boolean hungry = wallet.food() < ledger.foodUpkeep();
        wallet.pay(new Cost(Math.min(wallet.gold(), ledger.goldUpkeep()), Math.min(wallet.food(), ledger.foodUpkeep()), 0));
        if (unpaid) { house.army().changeMorale(-10); state.log(owner.displayName() + " cannot pay its soldiers."); }
        if (hungry) {
            house.army().changeMorale(-15); house.army().lose(1);
            state.log(owner.displayName() + " loses a detachment to hunger.");
        }
        return ledger;
    }
}
