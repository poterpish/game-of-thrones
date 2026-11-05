package edu.wotfk.units;

import edu.wotfk.domain.*;
import edu.wotfk.campaign.*;
import edu.wotfk.game.*;
import edu.wotfk.economy.*;
import static edu.wotfk.testsupport.TestSupport.*;
public final class DecoratorChecks {
    public static void main(String[] args) {
        CampaignState s = new CampaignDirector().standard(HouseId.STARK, 1); Army army = s.house(HouseId.STARK).army();
        Unit base = army.units().get(0);
        Unit decorated = new ExhaustedUnit(new CommanderSupportedUnit(new EquippedUnit(base)));
        equal((base.attack()+4)*3/4, decorated.attack(), "Decorators compose bonuses then exhaustion");
        equal(base.id(), decorated.id(), "Decorator preserves unit identity");
        equal(base.goldUpkeep()+1, decorated.goldUpkeep(), "Equipment modifies upkeep");
        GameFacade game = new GameFacade(s);
        check(game.equip(1).accepted(), "Equipment is a real paid action");
        check(!game.equip(1).accepted(), "Duplicate equipment rejected");
        check(game.command().accepted(), "Commander effect applied");
        equal(base.attack()+4, army.effectiveUnits(1).get(0).attack(), "Both active effects reach combat values");
        equal(base.attack()+2, army.effectiveUnits(3).get(0).attack(), "Commander expires while equipment remains");
        equal(base.attack(), army.units().get(0).attack(), "Permanent base product stays unchanged");
        equal(2, new EconomyService().forecast(s,HouseId.STARK).goldUpkeep(), "Visitor accounts for decorated upkeep");
        check(!game.equip(99).accepted(), "Invalid unit index changes nothing");
    }
}
