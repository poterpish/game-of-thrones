package edu.wotfk.ai;

import edu.wotfk.campaign.*;
import edu.wotfk.domain.*;
import edu.wotfk.diplomacy.*;
import edu.wotfk.intrigue.*;
import edu.wotfk.intelligence.*;
import edu.wotfk.story.*;
import static edu.wotfk.testsupport.TestSupport.*;
public final class PoliticalAiChecks {
    public static void main(String[] args){
        CampaignState s=new CampaignDirector().standard(HouseId.STARK,0);
        Relation relation=s.diplomacy().between(HouseId.STARK,HouseId.BARATHEON);relation.status(Relation.Status.ALLIED);relation.changeTrust(50);
        s.house(HouseId.STARK).changeReputation(-60);new PoliticalAi().act(s,HouseId.BARATHEON);
        check(!s.diplomacy().allied(HouseId.STARK,HouseId.BARATHEON),"AI reconsiders an ally with collapsed reputation");
        CampaignState plot=new CampaignDirector().standard(HouseId.STARK,0);
        plot.house(HouseId.LANNISTER).army().moveTo("LP");plot.house(HouseId.STARK).army().moveTo("SG");
        Relation pact=plot.diplomacy().between(HouseId.LANNISTER,HouseId.STARK);pact.status(Relation.Status.ALLIED);pact.changeTrust(65);
        FeastService feasts=new FeastService();feasts.invite(plot,HouseId.LANNISTER,HouseId.STARK);feasts.accept(plot,HouseId.STARK,1);
        new PoliticalAi().act(plot,HouseId.LANNISTER);
        check(plot.feasts().get(0).conspiracy()!=null&&plot.feasts().get(0).conspiracy().prepared(),"A low-reputation AI host can organize a real plot");
        check(!feasts.overview(plot,HouseId.STARK).contains("Private plan"),"AI secrets are not exposed by the player view");
        new IntelligenceService().spy(plot,HouseId.STARK,HouseId.LANNISTER);
        check(plot.knowledge().hasEvidence(HouseId.STARK,1),"Player can discover an AI conspiracy through normal scouting");
        CampaignState recovery=new CampaignDirector().standard(HouseId.STARK,1);
        recovery.house(HouseId.STARK).army().moveTo("TW");recovery.house(HouseId.STARK).army().lose(3);
        new edu.wotfk.combat.ArmyRecoveryService().recover(recovery,HouseId.STARK);
        equal("WF",recovery.house(HouseId.STARK).army().location(),"An unarmed ruler can rebuild after being stranded");
        equal(0,recovery.house(HouseId.STARK).army().units().size(),"Recovery does not create or teleport troops");
    }
}
