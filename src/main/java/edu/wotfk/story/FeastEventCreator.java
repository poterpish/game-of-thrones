package edu.wotfk.story;

import edu.wotfk.campaign.CampaignState;
import edu.wotfk.domain.HouseId;
import edu.wotfk.intrigue.Feast;
public final class FeastEventCreator extends EventCreator {
    @Override public String key(){return "feast-invitation";}
    private Feast pending(CampaignState state,HouseId guest){return state.feasts().stream().filter(f->f.guest()==guest&&f.status()==Feast.Status.INVITED&&state.round()<=f.meetingRound()).findFirst().orElse(null);}
    @Override protected boolean eligible(CampaignState state,HouseId guest){return pending(state,guest)!=null;}
    @Override protected String occurrenceKey(CampaignState state,HouseId guest){Feast f=pending(state,guest);return key()+":"+(f==null?"none":f.id());}
    @Override protected StoryEvent createEvent(CampaignState state,HouseId guest){Feast f=pending(state,guest);return new FeastInvitationEvent(f.id(),f.host(),f.castle(),f.meetingRound());}
}
