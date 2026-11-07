package edu.wotfk.intrigue;

import edu.wotfk.campaign.*;
import edu.wotfk.domain.*;
import edu.wotfk.game.ActionService;
import edu.wotfk.story.*;
import static edu.wotfk.testsupport.TestSupport.*;
public final class WeddingChecks {
    private static CampaignState prepared(long seed){
        CampaignState s=FeastChecks.fixture(seed);FeastService f=new FeastService();
        f.invite(s,HouseId.STARK,HouseId.TARGARYEN);f.accept(s,HouseId.TARGARYEN,1);
        new ActionService().move(s,HouseId.TARGARYEN,"WF");new ConspiracyService().prepare(s,HouseId.STARK,1);
        return s;
    }
    public static void main(String[] args){
        CampaignState success=prepared(0);success.nextRound();success.nextRound();new FeastService().resolveDue(success);
        equal(Conspiracy.Status.EXECUTED,success.feasts().get(0).conspiracy().status(),"Verified seed resolves a successful ambush");
        equal(2,success.house(HouseId.TARGARYEN).army().units().size(),"Ambush removes half of three detachments rounded down");
        equal(40,success.house(HouseId.TARGARYEN).army().morale(),"Successful ambush damages actual morale");
        equal(HouseId.STARK,success.map().territory("WF").owner(),"Host castle owner does not change");
        equal(HouseId.TARGARYEN,success.map().territory("DS").owner(),"Victim territories are not magically annexed");
        equal("TW",success.house(HouseId.TARGARYEN).army().location(),"Survivors retreat along an existing adjacent route");
        equal(40,success.house(HouseId.STARK).reputation(),"Organizer pays the full betrayal penalty");
        int troops=success.house(HouseId.TARGARYEN).army().units().size();new FeastService().resolveDue(success);
        equal(troops,success.house(HouseId.TARGARYEN).army().units().size(),"Resolved ambush cannot execute again");
        CampaignState failed=prepared(1);failed.nextRound();failed.nextRound();new FeastService().resolveDue(failed);
        equal(Conspiracy.Status.FAILED,failed.feasts().get(0).conspiracy().status(),"Verified seed resolves a failed ambush");
        equal(3,failed.house(HouseId.TARGARYEN).army().units().size(),"Failed ambush does not remove guest troops");
        equal(2,failed.house(HouseId.STARK).army().units().size(),"Failure costs the organizer a detachment");
        check(failed.diplomacy().atWar(HouseId.STARK,HouseId.TARGARYEN),"A failed attempt still causes war");
        CampaignState abandoned=prepared(0);new ConspiracyService().abandon(abandoned,HouseId.STARK,1);
        abandoned.nextRound();abandoned.nextRound();new FeastService().resolveDue(abandoned);
        equal(Feast.Status.COMPLETED,abandoned.feasts().get(0).status(),"Abandoned plot can end in a peaceful feast");
        check(abandoned.diplomacy().allied(HouseId.STARK,HouseId.TARGARYEN),"Honoring guest right preserves the alliance");
        equal(7,abandoned.house(HouseId.STARK).resources().gold(),"Abandoning does not refund preparations");
        CampaignState defended=prepared(0);defended.nextRound();defended.house(HouseId.TARGARYEN).guardUntil(3);defended.nextRound();
        equal(43,new ConspiracyService().ambushChance(defended,defended.feasts().get(0)),"Counterintelligence reduces ambush probability");
        new FeastService().resolveDue(defended);equal(Conspiracy.Status.FAILED,defended.feasts().get(0).conspiracy().status(),"The same roll can be defeated by prepared security");
    }
}
