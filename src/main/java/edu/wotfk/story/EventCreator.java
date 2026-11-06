package edu.wotfk.story;

import edu.wotfk.campaign.CampaignState;
import edu.wotfk.domain.HouseId;
/** Factory Method: a common offer algorithm delegates product creation to a subclass. */
public abstract class EventCreator {
    public abstract String key();
    protected abstract boolean eligible(CampaignState state, HouseId house);
    protected abstract StoryEvent createEvent(CampaignState state, HouseId house);
    protected String occurrenceKey(CampaignState state, HouseId house) { return key(); }
    public final boolean offer(CampaignState state, HouseId house) {
        String marker = occurrenceKey(state, house) + ":" + house;
        if (state.finished() || !state.alive(house) || state.pendingEvents().containsKey(house) || state.offeredEvents().contains(marker) || !eligible(state, house)) return false;
        StoryEvent event = createEvent(state, house);
        state.pendingEvents().put(house, event); state.offeredEvents().add(marker);
        return true;
    }
}
