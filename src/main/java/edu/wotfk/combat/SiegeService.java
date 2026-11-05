package edu.wotfk.combat;

import edu.wotfk.campaign.CampaignState;
import edu.wotfk.domain.*;
import edu.wotfk.events.WorldNotice;
import static edu.wotfk.game.GameRules.*;
public final class SiegeService {
    public static boolean blockaded(CampaignState state, String id) { return state.sieges().values().stream().anyMatch(s -> s.target().equals(id)); }
    public String begin(CampaignState state, HouseId actor, String id) {
        new BattleService().validateTarget(state, actor, id);
        Territory target = state.map().territory(id);
        require(!state.sieges().containsKey(actor), "Your army already maintains a siege. Move or lift it first.");
        require(!blockaded(state, target.id()), "Another army is already besieging this castle.");
        pay(state, actor, new Cost(0, 2, 0), 1);
        state.sieges().put(actor, new Siege(actor, state.house(actor).army().location(), target.id()));
        state.events().publish(new WorldNotice("siege", state.round(), actor, actor.displayName() + " lays siege to " + target.name() + "."));
        return "Siege begun. Each completed round removes one fort level from the assault calculation (max 2). Income and recruitment are blocked.";
    }
    public String lift(CampaignState state, HouseId actor) {
        ready(state, actor, Cost.NONE, 0);
        require(state.sieges().remove(actor) != null, "No active siege to lift.");
        return "Siege lifted; its progress is lost.";
    }
    public void advance(CampaignState state) {
        state.sieges().values().removeIf(s -> !valid(state, s));
        for (Siege siege : state.sieges().values()) siege.advance();
    }
    public boolean valid(CampaignState state, Siege siege) {
        Army army = state.house(siege.attacker()).army(); HouseId owner = state.map().territory(siege.target()).owner();
        return state.alive(siege.attacker()) && !army.units().isEmpty() && army.location().equals(siege.origin())
                && owner != siege.attacker() && (owner == null || state.diplomacy().atWar(siege.attacker(), owner));
    }
}
