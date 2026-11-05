package edu.wotfk.story;

import edu.wotfk.campaign.CampaignState;
import edu.wotfk.domain.*;
import java.util.*;
import static edu.wotfk.game.GameRules.*;
public final class EventService {
    private final List<EventCreator> creators = List.of(new WinterEventCreator(), new CouncilEventCreator());
    public void offerAll(CampaignState state) { for (HouseId house : HouseId.values()) for (EventCreator creator : creators) creator.offer(state, house); }
    public String describe(CampaignState state, HouseId actor) {
        StoryEvent event = state.pendingEvents().get(actor);
        if (event == null) return "No story decision is waiting.";
        StringBuilder out = new StringBuilder(event.title()).append("\n").append(event.description()).append("\n");
        int number = 0; for (StoryChoice choice : event.choices()) out.append(++number).append(". ").append(choice.label()).append("\n");
        return out.append("Use choose NUMBER. Choices cost no AP. Ending the turn takes the last affordable option.").toString();
    }
    public String choose(CampaignState state, HouseId actor, int number) {
        StoryEvent event = state.pendingEvents().get(actor); require(event != null, "No pending story event.");
        return event.resolve(state, actor, number - 1);
    }
    public void resolveAutomatically(CampaignState state, HouseId actor) {
        StoryEvent event = state.pendingEvents().get(actor); if (event == null || !state.alive(actor)) return;
        List<StoryChoice> choices = event.choices();
        // Human defaults never unexpectedly spend scarce resources; AI prefers a useful affordable investment.
        if (actor != state.player() && state.house(actor).resources().canPay(choices.get(0).cost())) { event.resolve(state, actor, 0); return; }
        for (int i = choices.size()-1; i >= 0; i--) if (state.house(actor).resources().canPay(choices.get(i).cost())) { event.resolve(state, actor, i); return; }
    }
}
