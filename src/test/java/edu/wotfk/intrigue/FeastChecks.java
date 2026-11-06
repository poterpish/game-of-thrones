package edu.wotfk.intrigue;

import edu.wotfk.campaign.*;
import edu.wotfk.domain.*;
import edu.wotfk.diplomacy.*;
import edu.wotfk.game.*;
import edu.wotfk.story.*;
import static edu.wotfk.testsupport.TestSupport.*;
public final class FeastChecks {
    public static CampaignState fixture(long seed){
        CampaignState s=new CampaignDirector().standard(HouseId.STARK,seed);
        Relation r=s.diplomacy().between(HouseId.STARK,HouseId.TARGARYEN);r.status(Relation.Status.ALLIED);r.changeTrust(30);
        s.house(HouseId.TARGARYEN).army().moveTo("WH");return s;
    }
    public static void main(String[] args){
        CampaignState s=fixture(1);FeastService service=new FeastService();
        service.invite(s,HouseId.STARK,HouseId.TARGARYEN);
        check(s.pendingEvents().get(HouseId.TARGARYEN) instanceof FeastInvitationEvent,"Invitation uses the shared Factory Method story system");
        equal(10,s.house(HouseId.STARK).resources().gold(),"Invitation pays its preparation cost");
        new EventService().choose(s,HouseId.TARGARYEN,1);
        equal("WH",s.house(HouseId.TARGARYEN).army().location(),"Acceptance never teleports the guest");
        new ActionService().move(s,HouseId.TARGARYEN,"WF");
        equal(Feast.Status.ACCEPTED,s.feasts().get(0).status(),"Accepted feast waits for its scheduled round");
        int trust=s.diplomacy().between(HouseId.STARK,HouseId.TARGARYEN).trust();
        s.nextRound();s.nextRound();service.resolveDue(s);
        equal(Feast.Status.COMPLETED,s.feasts().get(0).status(),"Prepared meeting resolves when participants arrive");
        equal(trust+20,s.diplomacy().between(HouseId.STARK,HouseId.TARGARYEN).trust(),"Peaceful feast improves real trust");
        int influence=s.house(HouseId.STARK).resources().influence();service.resolveDue(s);
        equal(influence,s.house(HouseId.STARK).resources().influence(),"Meeting rewards cannot be claimed twice");
        CampaignState absent=fixture(1);service.invite(absent,HouseId.STARK,HouseId.TARGARYEN);service.accept(absent,HouseId.TARGARYEN,1);
        absent.nextRound();absent.nextRound();service.resolveDue(absent);
        equal(Feast.Status.CANCELLED,absent.feasts().get(0).status(),"Absent armies cause cancellation, not remote casualties");
        CampaignState bad=new CampaignDirector().standard(HouseId.GREYJOY,1);
        rejects(()->service.invite(bad,HouseId.GREYJOY,HouseId.STARK),"Untrusted invitations require political preparation");
        equal(18,bad.house(HouseId.GREYJOY).resources().gold(),"Invalid feast spends nothing");
    }
}
