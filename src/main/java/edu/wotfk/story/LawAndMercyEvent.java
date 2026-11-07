package edu.wotfk.story;

import edu.wotfk.domain.*;
import java.util.List;
public final class LawAndMercyEvent extends DecisionEvent {
    public LawAndMercyEvent(){super("law-mercy","LAW AND MERCY","The storm lords demand a clear answer for the regency loyalists. Mercy costs money but may restore supply routes. Punishment offers immediate proceeds while alienating the remaining courts.",List.of(
        new StoryChoice("Pardon: 3 gold, +8 reputation/+4 trust; +6 food in two rounds",new Cost(3,0,0),c->{c.house().changeReputation(8);c.state().diplomacy().changeOthersTrust(c.actor(),4);c.flag("mercy");new ConsequenceScheduler().schedule(c.state(),c.actor(),2,ScheduledConsequence.Kind.FOOD,6,"Pardoned villages reopen their supply routes; food +6.");},"The law is tempered by a public pardon."),
new StoryChoice("Punish: +8 gold/+5 morale, -8 reputation/-6 trust; backlash in two rounds",Cost.NONE,c->{c.house().resources().gain(8,0,0);c.house().army().changeMorale(5);c.house().changeReputation(-8);c.state().diplomacy().changeOthersTrust(c.actor(),-6);c.flag("judgment");new ConsequenceScheduler().schedule(c.state(),c.actor(),2,ScheduledConsequence.Kind.BACKLASH,4,"Harsh judgments unsettle neighboring lords; reputation -5 and trust -4.");},"The storm court demands punishment rather than reconciliation.")
    ));}
}
