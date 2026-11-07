package edu.wotfk.intrigue;

import edu.wotfk.campaign.*;
import edu.wotfk.domain.*;
import edu.wotfk.intelligence.*;
import edu.wotfk.story.*;
import edu.wotfk.economy.*;
import static edu.wotfk.testsupport.TestSupport.*;
public final class ConspiracyChecks {
    public static void main(String[] args){
        CampaignState s=FeastChecks.fixture(0);FeastService f=new FeastService();ConspiracyService c=new ConspiracyService();
        f.invite(s,HouseId.STARK,HouseId.TARGARYEN);
        rejects(()->c.prepare(s,HouseId.STARK,1),"Conspiracy requires the guest's agreement first");
        f.accept(s,HouseId.TARGARYEN,1);c.prepare(s,HouseId.STARK,1);
        check(s.feasts().get(0).conspiracy().prepared(),"Secret preparation is recorded");
        check(!f.overview(s,HouseId.TARGARYEN).contains("Private plan"),"Guest cannot read the host's secret plot");
        rejects(()->c.expose(s,HouseId.TARGARYEN,1),"Suspicion without evidence is insufficient");
        new IntelligenceService().spy(s,HouseId.TARGARYEN,HouseId.STARK);
        check(s.knowledge().hasEvidence(HouseId.TARGARYEN,1),"Successful spying discovers actual preparations");
        c.expose(s,HouseId.TARGARYEN,1);
        equal(Conspiracy.Status.EXPOSED,s.feasts().get(0).conspiracy().status(),"Evidence stops the plot");
        equal(Feast.Status.CANCELLED,s.feasts().get(0).status(),"Exposed meeting is cancelled");
        check(s.diplomacy().atWar(HouseId.STARK,HouseId.TARGARYEN),"Exposure has diplomatic consequences");
        int rep=s.house(HouseId.STARK).reputation();
        new ConsequenceScheduler().applyDue(s);equal(rep,s.house(HouseId.STARK).reputation(),"Backlash does not fire early");
        s.nextRound();s.nextRound();new ConsequenceScheduler().applyDue(s);
        equal(rep-5,s.house(HouseId.STARK).reputation(),"Political backlash arrives after two rounds");
        new ConsequenceScheduler().applyDue(s);equal(rep-5,s.house(HouseId.STARK).reputation(),"Delayed consequence executes exactly once");
        CampaignState loan=new CampaignDirector().standard(HouseId.STARK,1);new LoanService().borrow(loan,HouseId.STARK);
        equal(24,loan.house(HouseId.STARK).resources().gold(),"Loan gives its immediate gold");
        rejects(()->new LoanService().borrow(loan,HouseId.STARK),"Parallel loans are forbidden");
        loan.nextRound();loan.nextRound();loan.nextRound();new ConsequenceScheduler().applyDue(loan);
        equal(9,loan.house(HouseId.STARK).resources().gold(),"Mature debt repays the promised amount");
    }
}
