package edu.wotfk.game;

import edu.wotfk.campaign.CampaignState;
import edu.wotfk.domain.*;
import static edu.wotfk.game.GameRules.*;
/** Economic and movement rules shared by player and computer houses. */
public final class ActionService {
    public String recruit(CampaignState state, HouseId actor, UnitType type) {
        HouseState house = state.house(actor); Army army = house.army();
        require(state.map().territory(army.location()).owner() == actor, "Recruit in one of your own territories.");
        require(!edu.wotfk.combat.SiegeService.blockaded(state, army.location()), "Recruitment is blocked by a siege.");
        require(army.units().size() < Army.CAPACITY, "Army capacity is six detachments.");
        Cost cost = house.factory().recruitmentCost(type);
        pay(state, actor, cost, 1); army.add(house.newUnit(type));
        state.log(actor.displayName() + " recruits a " + type.name().toLowerCase(java.util.Locale.ROOT) + " detachment.");
        return "Recruited " + type + ". " + house.resources().summary();
    }
    public String move(CampaignState state, HouseId actor, String destination) {
        HouseState house = state.house(actor); Army army = house.army(); Territory target = state.map().territory(destination);
        require(!army.units().isEmpty(), "Recruit troops before marching.");
        WorldMap.Route route = state.map().route(army.location(), target.id()).orElseThrow(() -> new IllegalStateException("Destination is not adjacent."));
        require(target.owner() == actor || state.diplomacy().allied(actor, target.owner()) || new edu.wotfk.intrigue.FeastService().canVisit(state, actor, target.id()), "March only through your own or allied territory. Attack hostile castles instead.");
        int food = route.sea() && actor != HouseId.GREYJOY ? 2 : 1;
        pay(state, actor, new Cost(0, food, 0), 1);
        army.moveTo(target.id()); state.sieges().remove(actor);
        state.log(actor.displayName() + " marches to " + target.name() + ".");
        return "Army moved to " + target.name() + "; spent " + food + " food and 1 action.";
    }
    public String fortify(CampaignState state, HouseId actor, String id) {
        Territory territory = state.map().territory(id);
        require(territory.owner() == actor, "You do not own that territory.");
        require(!edu.wotfk.combat.SiegeService.blockaded(state, territory.id()), "Cannot reinforce a blockaded castle.");
        require(territory.garrison() < 8, "Garrison capacity is eight.");
        pay(state, actor, new Cost(3, 1, 0), 1); territory.reinforce();
        return territory.name() + " garrison increased to " + territory.garrison() + ".";
    }
    public String supply(CampaignState state, HouseId actor) {
        pay(state, actor, new Cost(3, 0, 0), 1); state.house(actor).resources().gain(0, 6, 0);
        return "Bought 6 food for 3 gold and 1 action.";
    }
    public String rally(CampaignState state, HouseId actor) {
        require(state.house(actor).army().morale() < 100, "Morale is already at its maximum.");
        pay(state, actor, new Cost(0, 2, 0), 1); state.house(actor).army().changeMorale(15);
        return "Morale increased by up to 15 for 2 food and 1 action.";
    }
}
