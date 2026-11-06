package edu.wotfk.diplomacy;

import edu.wotfk.campaign.*;
import edu.wotfk.domain.*;
import edu.wotfk.game.*;
import edu.wotfk.economy.*;
import static edu.wotfk.testsupport.TestSupport.*;
public final class DiplomacyChecks {
    public static void main(String[] args) {
        CampaignState s=new CampaignDirector().standard(HouseId.STARK,1);GameFacade g=new GameFacade(s);
        check(g.ally(HouseId.BARATHEON,0).accepted(),"Alliance negotiation is a legal action");
        check(s.diplomacy().allied(HouseId.STARK,HouseId.BARATHEON),"Trust and reputation can produce an alliance");
        equal(6,new EconomyService().forecast(s,HouseId.STARK).goldIncome(),"Allied trade adds one gold per round");
        check(!g.ally(HouseId.BARATHEON,0).accepted(),"Duplicate alliance rejected");
        check(g.breakAlliance(HouseId.BARATHEON).accepted(),"Alliance can be peacefully terminated");
        int reputation=s.house(HouseId.STARK).reputation();
        check(!g.war(HouseId.BARATHEON).accepted(),"Break-then-attack loophole is blocked by a truce");
        int trust=s.diplomacy().between(HouseId.STARK,HouseId.TARGARYEN).trust();
        check(g.betray(HouseId.BARATHEON).accepted(),"Explicit truce betrayal is possible");
        equal(reputation-30,s.house(HouseId.STARK).reputation(),"Betrayal damages reputation");
        equal(trust-12,s.diplomacy().between(HouseId.STARK,HouseId.TARGARYEN).trust(),"Third parties remember betrayal");
        check(s.diplomacy().atWar(HouseId.STARK,HouseId.BARATHEON),"Betrayal starts war");
        CampaignState p=new CampaignDirector().standard(HouseId.STARK,1);DiplomacyService d=new DiplomacyService();
        p.house(HouseId.STARK).resources().gain(50,10,20);
        d.alliance(p,HouseId.STARK,HouseId.BARATHEON,0);d.requestEndorsement(p,HouseId.STARK,HouseId.BARATHEON);
        equal(HouseId.STARK,p.diplomacy().endorsements().get(HouseId.BARATHEON),"An ally can endorse a claimant");
        rejects(()->d.requestEndorsement(p,HouseId.STARK,HouseId.BARATHEON),"One endorsement per house");
        p.house(HouseId.STARK).resetActions();d.alliance(p,HouseId.STARK,HouseId.TARGARYEN,0);d.requestEndorsement(p,HouseId.STARK,HouseId.TARGARYEN);
        p.map().territory("KL").capture(HouseId.STARK,1);p.nextRound();p.nextRound();new VictoryService().evaluate(p);
        check(p.finished()&&p.ending().contains("recognized crown"),"Two allies and a held capital can win politically");
        CampaignState aid=new CampaignDirector().standard(HouseId.STARK,1);
        d.alliance(aid,HouseId.STARK,HouseId.BARATHEON,0);aid.diplomacy().between(HouseId.STARK,HouseId.LANNISTER).status(Relation.Status.WAR);
        d.requestAid(aid,HouseId.STARK,HouseId.BARATHEON);d.answerAiRequest(aid,HouseId.BARATHEON);
        check(aid.diplomacy().aidRequests().isEmpty(),"Allied AI fulfills an affordable obligation");
        equal(16,aid.house(HouseId.STARK).resources().gold(),"Aid transfers actual resources");
        CampaignState refusal=new CampaignDirector().standard(HouseId.GREYJOY,1);
        int gold=refusal.house(HouseId.GREYJOY).resources().gold();d.alliance(refusal,HouseId.GREYJOY,HouseId.STARK,1);
        check(!refusal.diplomacy().allied(HouseId.GREYJOY,HouseId.STARK),"Poor political offer can fail");
        equal(gold-1,refusal.house(HouseId.GREYJOY).resources().gold(),"Legal failed negotiations still spend their gift");
    }
}
