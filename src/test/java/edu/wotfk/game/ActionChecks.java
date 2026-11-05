package edu.wotfk.game;

import edu.wotfk.campaign.*;
import edu.wotfk.domain.*;
import static edu.wotfk.testsupport.TestSupport.*;
public final class ActionChecks {
    public static void main(String[] args) {
        CampaignState s = new CampaignDirector().standard(HouseId.STARK, 1); GameFacade game = new GameFacade(s);
        check(!game.move("KL").accepted(), "Non-adjacent movement rejected");
        equal(3, s.house(HouseId.STARK).actions(), "Invalid move preserves AP");
        check(game.recruit(UnitType.MILITIA).accepted(), "Affordable recruitment accepted");
        equal(4, s.house(HouseId.STARK).army().units().size(), "Recruitment adds a detachment");
        equal(9, s.house(HouseId.STARK).resources().gold(), "Recruitment spends exact price");
        check(game.fortify("WF").accepted(), "Own garrison reinforced");
        check(!game.fortify("CR").accepted(), "Enemy garrison cannot be reinforced");
        check(game.move("WH").accepted(), "Own adjacent territory reachable");
        check(!game.supply().accepted(), "Fourth action rejected");
        equal(0, s.house(HouseId.STARK).actions(), "Actions never become negative");
        CampaignState sea = new CampaignDirector().standard(HouseId.GREYJOY, 1);
        new GameFacade(sea).move("SG"); equal(13, sea.house(HouseId.GREYJOY).resources().food(), "Greyjoy pays one food at sea");
        CampaignState dragon = new CampaignDirector().standard(HouseId.TARGARYEN, 1);
        new GameFacade(dragon).move("DD"); equal(14, dragon.house(HouseId.TARGARYEN).resources().food(), "Other houses pay two food at sea");
        check(new GameFacade(dragon).war(HouseId.BARATHEON).accepted(), "War can be declared");
        check(!new GameFacade(dragon).war(HouseId.BARATHEON).accepted(), "Duplicate war rejected");
        s.finish(null, "Test ending"); check(!game.recruit(UnitType.MILITIA).accepted(), "Finished campaign rejects commands");
    }
}
