package edu.wotfk.ui;

import edu.wotfk.campaign.CampaignState;
import edu.wotfk.domain.*;
/** Read-only text views; only the player's own army and balances are shown exactly. */
public final class CampaignView {
    private CampaignView() { }
    public static String status(CampaignState state) {
        HouseState house = state.house(state.player()); Army army = house.army();
        String lands = String.join(", ", state.map().ownedBy(house.id()).stream().map(Territory::name).toList());
        return "HOUSE " + house.id().name() + "   Round " + state.round() + "/" + state.maxRounds() + "   Actions " + house.actions()
                + "\n" + house.resources().summary() + "   Reputation " + house.reputation()
                + "\nArmy: " + army.units().size() + "/6 at " + state.map().territory(army.location()).name() + "   Morale " + army.morale()
                + "\nSiege: " + (state.sieges().get(house.id()) == null ? "none" : state.sieges().get(house.id()).target() + " (progress " + state.sieges().get(house.id()).progress() + "/2)")
                + "\nLands: " + lands + "\nStrength: " + house.factory().strengths() + "\nRisk: " + house.factory().weakness();
    }
}
