package edu.wotfk.units;

import edu.wotfk.campaign.CampaignState;
import edu.wotfk.domain.*;
import static edu.wotfk.game.GameRules.*;
public final class EquipmentService {
    public String equip(CampaignState state, HouseId actor, int index) {
        Army army = state.house(actor).army();
        require(index >= 1 && index <= army.units().size(), "Choose an existing unit number from army.");
        Unit unit = army.units().get(index - 1);
        require(!army.equipped(unit.id()), "That unit is already equipped.");
        require(state.map().territory(army.location()).owner() == actor, "Equip troops at an owned castle.");
        pay(state, actor, new Cost(3,0,0), 1); army.equip(unit.id());
        return "Equipped " + unit.name() + ": +2 attack, +2 defense and +1 gold upkeep.";
    }
    public String command(CampaignState state, HouseId actor) {
        Army army = state.house(actor).army();
        require(!army.units().isEmpty(), "No troops to command.");
        require(!army.commanded(state.round()), "Commander support is already active.");
        pay(state, actor, new Cost(0,0,2), 1); army.supportUntil(state.round() + 1);
        return "Commander support: +2 attack/+1 defense per unit through round " + (state.round() + 1) + ".";
    }
    public String overview(CampaignState state, HouseId actor) {
        StringBuilder out = new StringBuilder("NO  ATT DEF GOLD/FOOD  UNIT AND EFFECTS\n"); int index = 0;
        for (Unit unit : state.house(actor).army().effectiveUnits(state.round()))
            out.append(String.format("%-3d %3d %3d %4d/%-4d %s%n", ++index, unit.attack(), unit.defense(), unit.goldUpkeep(), unit.foodUpkeep(), unit.name()));
        return out.toString();
    }
}
