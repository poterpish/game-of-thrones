package edu.wotfk.story;

import edu.wotfk.domain.*;
import java.util.List;
public final class RestorationEvent extends DecisionEvent {
    public RestorationEvent(){super("restoration","THE PRICE OF RETURN","The returning dynasty must decide what restoration means. Amnesty can turn old enemies into future supporters. Confiscation funds the army, but creates enemies whose anger will spread.",List.of(
        new StoryChoice("Amnesty: 5 gold/2 influence, +12 reputation and +8 trust; +4 trust in two rounds",new Cost(5,0,2),c->{c.house().changeReputation(12);c.state().diplomacy().changeOthersTrust(c.actor(),8);c.flag("amnesty");new ConsequenceScheduler().schedule(c.state(),c.actor(),2,ScheduledConsequence.Kind.TRUST,4,"The promised amnesty is honored; trust rises by 4.");},"The returning dynasty offers a genuine amnesty."),
new StoryChoice("Confiscate +10 gold: -10 reputation/-5 trust now, further backlash in two rounds",Cost.NONE,c->{c.house().resources().gain(10,0,0);c.house().changeReputation(-10);c.state().diplomacy().changeOthersTrust(c.actor(),-5);c.flag("confiscation");new ConsequenceScheduler().schedule(c.state(),c.actor(),2,ScheduledConsequence.Kind.BACKLASH,6,"Families displaced by confiscations organize opposition; reputation -5 and trust -6.");},"The returning dynasty seizes its opponents' wealth.")
    ));}
}
