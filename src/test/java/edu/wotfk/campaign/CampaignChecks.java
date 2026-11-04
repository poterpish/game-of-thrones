package edu.wotfk.campaign;

import edu.wotfk.domain.*;
import edu.wotfk.houses.*;
import edu.wotfk.data.*;
import static edu.wotfk.testsupport.TestSupport.*;
public final class CampaignChecks {
    public static void main(String[] args) {
        rejects(() -> new CampaignBuilder().build(), "Incomplete builder rejected");
        CampaignState a = new CampaignDirector().standard(HouseId.STARK, 42);
        CampaignState b = new CampaignDirector().standard(HouseId.STARK, 42);
        equal(a.random().nextInt(), b.random().nextInt(), "Equal seeds reproduce the random stream");
        for (HouseId id : HouseId.values()) {
            HouseState house = a.house(id);
            equal(id, house.diplomat().house(), id + " diplomat belongs to its family");
            equal(id, house.scout().house(), id + " scout belongs to its family");
            check(house.army().units().stream().allMatch(u -> u.house() == id), id + " troops belong to their family");
            equal(3, house.army().units().size(), id + " starts with three detachments");
        }
        check(a.house(HouseId.LANNISTER).resources().gold() > a.house(HouseId.STARK).resources().gold(), "Starting economies differ");
        check(a.house(HouseId.GREYJOY).scout().intelligenceBonus() > a.house(HouseId.BARATHEON).scout().intelligenceBonus(), "Scouting families differ mechanically");
        rejects(() -> a.house(HouseId.STARK).army().add(a.house(HouseId.LANNISTER).newUnit(UnitType.GUARD)), "Foreign-family recruitment rejected");
        a.house(HouseId.STARK).resources().pay(new Cost(1, 0, 0));
        equal(12, b.house(HouseId.STARK).resources().gold(), "Campaigns do not share mutable wallets");
    }
}
