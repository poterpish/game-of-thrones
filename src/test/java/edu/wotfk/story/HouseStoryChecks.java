package edu.wotfk.story;

import edu.wotfk.campaign.*;
import edu.wotfk.domain.*;
import edu.wotfk.game.*;
import edu.wotfk.houses.*;
import static edu.wotfk.testsupport.TestSupport.*;
public final class HouseStoryChecks {
    public static void main(String[] args){
        for(HouseId id:HouseId.values()){
            CampaignState s=new CampaignDirector().standard(id,1);s.nextRound();s.nextRound();new EventService().offerAll(s);
            StoryEvent story=s.pendingEvents().get(id);check(story!=null,"Unique house event is eligible for "+id);
            check(!story.id().equals("winter"),id+" receives its own perspective, not a generic winter event");
            new EventService().choose(s,id,1);check(!s.storyFlags().isEmpty(),id+" decision changes narrative flags");
            check(s.consequences().stream().anyMatch(c->c.house()==id),id+" decision creates a delayed consequence");
            equal(3,s.house(id).actions(),id+" event choice preserves action points");
        }
        CampaignState stark=new CampaignDirector().standard(HouseId.STARK,1);GameFacade game=new GameFacade(stark);
        check(game.special(null).accepted(),"Unique house ability is playable");
        equal(28,stark.house(HouseId.STARK).resources().food(),"Northern resolve adds actual food");
        check(!game.special(null).accepted(),"House ability cannot repeat before its cooldown");
        CampaignState grey=new CampaignDirector().standard(HouseId.GREYJOY,1);GameFacade raider=new GameFacade(grey);
        check(!raider.special("LP").accepted(),"Raids cannot bypass declaration of war");
        raider.war(HouseId.LANNISTER);check(raider.special("LP").accepted(),"Greyjoy can raid a hostile adjacent port");
        equal(24,grey.house(HouseId.GREYJOY).resources().gold(),"Raid transfers real treasury gold");
        equal(22,grey.house(HouseId.LANNISTER).resources().gold(),"Raided opponent loses the same gold");
        check(!grey.map().territory("LP").productive(1),"Raid damages port income");
        check(grey.map().territory("LP").productive(3),"Port recovers after two settlements");
    }
}
