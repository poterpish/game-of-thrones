package edu.wotfk.combat;

import edu.wotfk.campaign.CampaignState;
import edu.wotfk.domain.*;
import static edu.wotfk.game.GameRules.*;
public final class BattleService {
    private final BattleResolver resolver = new BattleResolver();
    public void validateTarget(CampaignState state, HouseId actor, String id) {
        Territory target = state.map().territory(id); Army army = state.house(actor).army();
        require(!army.units().isEmpty(), "Recruit troops before fighting.");
        require(state.map().route(army.location(), target.id()).isPresent(), "Attack an adjacent territory.");
        require(target.owner() != actor, "You already own that territory.");
        require(target.owner() == null || state.diplomacy().atWar(actor, target.owner()), "Declare war first; allied and neutral houses are protected.");
    }
    /** Shared resolution for a field skirmish and a later castle assault. */
    public BattleOutcome fight(CampaignState state, HouseId actor, String id, BattleStrategy strategy, int fortReduction) {
        validateTarget(state, actor, id);
        Territory target = state.map().territory(id); Army attacker = state.house(actor).army();
        Army defender = target.owner() == null ? null : state.house(target.owner()).army();
        if (defender != null && !defender.location().equals(target.id())) defender = null;
        boolean coastal = state.map().coastal(target.id());
        int defense = target.garrison() * 3 + Math.max(0, target.fort() - fortReduction) * 4;
        if (defender != null) defense += defender.defenseStrength(state.round(), coastal) * (50 + defender.morale()) / 100;
        pay(state, actor, new Cost(0, 2, 0), 2);
        BattleOutcome result = resolver.resolve(attacker.attackStrength(state.round(), coastal), defense, attacker.morale(), attacker.units().size(), defender == null ? 0 : defender.units().size(), strategy, state.random());
        attacker.lose(result.attackerLosses());
        if (!result.victory()) attacker.exhaustUntil(state.round() + 1);
        attacker.changeMorale(result.victory() ? 6 : -12);
        if (defender != null) { defender.lose(result.defenderLosses()); defender.changeMorale(result.victory() ? -15 : 8); }
        new ArmyRecoveryService().recover(state,actor);
        target.reduceGarrison(result.victory() ? 2 : 1);
        return result;
    }
    public String skirmish(CampaignState state, HouseId actor, String id, BattleStrategy strategy) {
        BattleOutcome result = fight(state, actor, id, strategy, 0);
        state.log(actor.displayName() + " fights a " + (result.victory() ? "successful" : "failed") + " skirmish near " + state.map().territory(id).name() + ".");
        return result.summary() + " A skirmish weakens defenders but does not capture their castle.";
    }
}
