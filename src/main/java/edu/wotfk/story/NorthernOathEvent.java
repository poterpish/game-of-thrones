package edu.wotfk.story;

import edu.wotfk.domain.*;
import java.util.List;
public final class NorthernOathEvent extends DecisionEvent {
    public NorthernOathEvent(){super("northern-oath","THE NORTHERN OATH","The northern lords will provision the war voluntarily if the crown pays them. Requisitioning grain feeds the army today but undermines the oaths that hold it together.",List.of(
        new StoryChoice("Pay 5 gold: +8 food now, +10 morale in two rounds",new Cost(5,0,0),c->{c.house().resources().gain(0,8,0);c.flag("oaths");new ConsequenceScheduler().schedule(c.state(),c.actor(),2,ScheduledConsequence.Kind.MORALE,10,"Northern provisioners honor their voluntary oath; morale +10.");},"The northern lords are paid, not coerced."),
new StoryChoice("Requisition +12 food: -8 reputation and -8 morale in two rounds",Cost.NONE,c->{c.house().resources().gain(0,12,0);c.flag("requisition");new ConsequenceScheduler().schedule(c.state(),c.actor(),2,ScheduledConsequence.Kind.LEVY_REACTION,8,"Requisitioned villages withdraw their goodwill; reputation and morale -8.");},"The North's granaries are requisitioned for the war.")
    ));}
}
