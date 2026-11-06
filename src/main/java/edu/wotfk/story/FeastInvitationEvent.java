package edu.wotfk.story;

import edu.wotfk.campaign.CampaignState;
import edu.wotfk.domain.*;
import edu.wotfk.intrigue.FeastService;
import java.util.List;
import static edu.wotfk.game.GameRules.*;
/** A product whose choices use the same validated operations as manual diplomacy commands. */
public final class FeastInvitationEvent implements StoryEvent {
    private final int feastId;
    private final HouseId host;
    private final String castle;
    private final int meetingRound;
    public FeastInvitationEvent(int feastId,HouseId host,String castle,int meetingRound){this.feastId=feastId;this.host=host;this.castle=castle;this.meetingRound=meetingRound;}
    public int feastId(){return feastId;}
    @Override public String id(){return "feast-"+feastId;}
    @Override public String title(){return "THE BREAD AND SALT";}
    @Override public String description(){return host.displayName()+" offers guest right at "+castle+" in round "+meetingRound+". You may accept, investigate, or refuse. Acceptance does not move your army.";}
    @Override public List<StoryChoice> choices(){return List.of(
        new StoryChoice("Accept: 1 food; travel separately",new Cost(0,1,0),c->new FeastService().accept(c.state(),c.actor(),feastId),"The invitation is accepted; travel to the host's castle."),
        new StoryChoice("Decline the invitation",Cost.NONE,c->new FeastService().decline(c.state(),c.actor(),feastId),"The invitation is declined.")
    );}
    @Override public String resolve(CampaignState state,HouseId actor,int choice){
        require(state.pendingEvents().get(actor)==this,"Invitation has already been answered.");
        require(choice>=0&&choice<choices().size(),"Choose a listed option.");
        StoryChoice selected=choices().get(choice);selected.effect().accept(new StoryContext(state,actor));
        return selected.result();
    }
}
