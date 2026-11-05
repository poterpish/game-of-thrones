package edu.wotfk.story;

import edu.wotfk.domain.Cost;
import java.util.List;
public final class CouncilWarningEvent extends DecisionEvent {
    public CouncilWarningEvent() {
        super("council", "THE LAST RAVENS", "The Great Council will decide the succession after round 12. Public promises improve reputation, but spend influence that also matters at the vote.", List.of(
            new StoryChoice("Promise a fair settlement: 2 influence -> +8 reputation", new Cost(0,0,2), c -> c.house().changeReputation(8), "A public pledge calms the surviving lords."),
            new StoryChoice("Preserve influence for the final negotiations", Cost.NONE, c -> c.flag("silent-council"), "The court keeps its final negotiating position private.")
        ));
    }
}
