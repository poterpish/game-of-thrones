package edu.wotfk.story;

import edu.wotfk.domain.Cost;
import java.util.List;
public final class WinterEvent extends DecisionEvent {
    public WinterEvent() {
        super("winter", "WINTER IS COMING", "From round 7, each non-empty army consumes one additional food per round. Stock the granaries or preserve political resources.", List.of(
            new StoryChoice("Stock the granaries: 5 gold -> 10 food", new Cost(5,0,0), c -> { c.house().resources().gain(0,10,0); c.flag("winter-ready"); }, "The granaries are stocked before winter."),
            new StoryChoice("Keep campaigning: gain 2 influence, no extra food", Cost.NONE, c -> c.house().resources().gain(0,0,2), "The court chooses political momentum over winter stores.")
        ));
    }
}
