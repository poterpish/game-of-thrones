package edu.wotfk.story;

import edu.wotfk.campaign.CampaignState;
import edu.wotfk.domain.*;
import java.util.List;
import static edu.wotfk.game.GameRules.*;
public abstract class DecisionEvent implements StoryEvent {
    private final String id;
    private final String title;
    private final String description;
    private final List<StoryChoice> choices;
    protected DecisionEvent(String id, String title, String description, List<StoryChoice> choices) {
        this.id = id; this.title = title; this.description = description; this.choices = List.copyOf(choices);
    }
    @Override public String id() { return id; }
    @Override public String title() { return title; }
    @Override public String description() { return description; }
    @Override public List<StoryChoice> choices() { return choices; }
    @Override public String resolve(CampaignState state, HouseId actor, int choice) {
        require(state.pendingEvents().get(actor) == this, "This event is not pending, or has already been resolved.");
        require(choice >= 0 && choice < choices.size(), "Choose a listed event option.");
        StoryChoice selected = choices.get(choice);
        pay(state, actor, selected.cost(), 0);
        selected.effect().accept(new StoryContext(state, actor));
        state.pendingEvents().remove(actor);
        state.log(actor.displayName() + ": " + selected.result());
        return selected.result();
    }
}
