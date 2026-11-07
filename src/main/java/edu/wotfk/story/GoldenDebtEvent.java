package edu.wotfk.story;

import edu.wotfk.domain.*;
import java.util.List;
public final class GoldenDebtEvent extends DecisionEvent {
    public GoldenDebtEvent(){super("golden-debt","THE CROWNS DEBTS","The western lenders offer money against a future crown. A loan buys a campaign now; default later will cost both reputation and influence. Austerity avoids the debt but saps morale.",List.of(
        new StoryChoice("Borrow 12 gold; repay 15 after three rounds (after income)",Cost.NONE,c->{c.house().resources().gain(12,0,0);c.flag("debt-crown");new ConsequenceScheduler().schedule(c.state(),c.actor(),3,ScheduledConsequence.Kind.DEBT,15,"Repay 15 gold; default seizes remaining gold and costs 20 reputation and up to 3 influence.");},"The lion signs a loan against the future crown."),
new StoryChoice("Choose austerity: -5 morale, +2 influence, no debt",Cost.NONE,c->{c.house().army().changeMorale(-5);c.house().resources().gain(0,0,2);c.flag("austerity");},"The court refuses the lenders and cuts its expenses.")
    ));}
}
