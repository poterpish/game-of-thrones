package edu.wotfk.story;

import edu.wotfk.domain.*;
import java.util.List;
public final class IronPriceEvent extends DecisionEvent {
    public IronPriceEvent(){super("iron-price","THE IRON PRICE","The captains can empty a port or protect its merchants. Protected trade builds a lasting claim to the coast; plunder pays immediately but halts the port economy and damages diplomatic trust.",List.of(
        new StoryChoice("Protect trade: 5 gold -> +5 food/+12 reputation now, +8 gold in two rounds",new Cost(5,0,0),c->{c.house().resources().gain(0,5,0);c.house().changeReputation(12);c.flag("safe-ports");new ConsequenceScheduler().schedule(c.state(),c.actor(),2,ScheduledConsequence.Kind.GOLD,8,"Protected coastal trade yields its first return; gold +8.");},"The captains accept protection duties instead of plunder."),
new StoryChoice("Plunder +12 gold: -10 reputation/-6 trust; one port loses income for two settlements",Cost.NONE,c->{c.house().resources().gain(12,0,0);c.house().changeReputation(-10);c.state().diplomacy().changeOthersTrust(c.actor(),-6);c.flag("plunder");c.state().map().ownedBy(c.actor()).stream().filter(t->c.state().map().coastal(t.id())).findFirst().ifPresent(t->t.damageUntil(c.state().round()+1));},"The captains take the iron price, leaving the port economy damaged.")
    ));}
}
