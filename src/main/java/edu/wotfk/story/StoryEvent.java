package edu.wotfk.story;

import edu.wotfk.campaign.CampaignState;
import edu.wotfk.domain.HouseId;
import java.util.List;
/** Product of the Factory Method hierarchy: an interactive pending decision. */
public interface StoryEvent {
    String id();
    String title();
    String description();
    List<StoryChoice> choices();
    String resolve(CampaignState state, HouseId actor, int choice);
}
