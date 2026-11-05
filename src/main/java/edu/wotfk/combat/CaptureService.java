package edu.wotfk.combat;

import edu.wotfk.campaign.CampaignState;
import edu.wotfk.domain.*;
import edu.wotfk.events.TerritoryCaptured;
/** Commits all territorial consequences before publishing the notification. */
public final class CaptureService {
    public void capture(CampaignState state, HouseId actor, String id) {
        Territory territory = state.map().territory(id); HouseId oldOwner = territory.owner();
        territory.capture(actor, state.round());
        state.house(actor).army().moveTo(territory.id());
        state.house(actor).resources().gain(2, 0, 1); state.house(actor).changeReputation(2);
        state.sieges().entrySet().removeIf(e -> e.getKey() == actor || e.getValue().target().equals(territory.id()));
        if (oldOwner != null) {
            Army defeated = state.house(oldOwner).army();
            if (defeated.location().equals(territory.id())) retreat(state, oldOwner, territory.id());
        }
        state.events().publish(new TerritoryCaptured(actor, oldOwner, territory.id(), state.round()));
    }
    private void retreat(CampaignState state, HouseId defender, String lostCastle) {
        Army army = state.house(defender).army();
        String destination = state.map().routes(lostCastle).stream().map(WorldMap.Route::destination)
                .filter(id -> state.map().territory(id).owner() == defender).findFirst().orElse(null);
        if (destination != null) {
            army.moveTo(destination); army.changeMorale(-8);
            state.log(defender.displayName() + " retreats to " + state.map().territory(destination).name() + ".");
        } else {
            army.lose(army.units().size());
            // With no safe adjacent retreat, the troops surrender; the ruler may rebuild at another holding.
            state.map().ownedBy(defender).stream().findFirst().ifPresent(t -> army.moveTo(t.id()));
            state.log(defender.displayName() + " has no safe retreat; its remaining troops surrender.");
        }
    }
}
