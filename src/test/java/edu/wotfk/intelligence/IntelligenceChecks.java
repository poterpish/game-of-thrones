package edu.wotfk.intelligence;

import edu.wotfk.campaign.*;
import edu.wotfk.domain.*;
import edu.wotfk.game.*;
import static edu.wotfk.testsupport.TestSupport.*;
public final class IntelligenceChecks {
    public static void main(String[] args){
        CampaignState s=new CampaignDirector().standard(HouseId.STARK,0);GameFacade g=new GameFacade(s);
        check(g.inspect("CR").contains("unknown"),"Enemy garrisons are hidden before scouting");
        check(g.intel(HouseId.LANNISTER).contains("No report"),"No live enemy resources leak before reconnaissance");
        check(g.spy(HouseId.LANNISTER).accepted(),"Spy mission is an affordable action");
        IntelReport observation=s.knowledge().report(HouseId.STARK,HouseId.LANNISTER);
        check(observation!=null,"Known seed successfully collects intelligence");
        equal(28,observation.gold(),"Report records the observed treasury");
        s.house(HouseId.LANNISTER).resources().gain(50,0,0);s.nextRound();
        equal(28,observation.gold(),"Later treasury changes do not rewrite the report");
        check(g.intel(HouseId.LANNISTER).contains("1 rounds old"),"Report clearly shows its age");
        check(g.inspect("CR").contains("round 1"),"Castle inspection labels historical garrison data");
        equal(10,s.house(HouseId.STARK).resources().gold(),"Mission costs exact gold");
        CampaignState failure=new CampaignDirector().standard(HouseId.STARK,1);
        new GameFacade(failure).spy(HouseId.LANNISTER);
        check(failure.knowledge().report(HouseId.STARK,HouseId.LANNISTER)==null,"Known failure seed creates no fabricated intelligence");
        equal(10,failure.house(HouseId.STARK).resources().gold(),"Failed legal mission still pays its price");
        int before=new IntelligenceService().chance(s,HouseId.STARK,HouseId.LANNISTER);
        new IntelligenceService().counterintelligence(s,HouseId.LANNISTER);
        equal(before-30,new IntelligenceService().chance(s,HouseId.STARK,HouseId.LANNISTER),"Counterintelligence changes actual success probability");
        check(!g.spy(HouseId.STARK).accepted(),"Cannot spy on yourself");
    }
}
