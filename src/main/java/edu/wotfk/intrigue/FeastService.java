package edu.wotfk.intrigue;

import edu.wotfk.campaign.CampaignState;
import edu.wotfk.domain.*;
import edu.wotfk.diplomacy.*;
import edu.wotfk.events.WorldNotice;
import static edu.wotfk.game.GameRules.*;
public final class FeastService {
    public Feast find(CampaignState state,int id){return state.feasts().stream().filter(f->f.id()==id).findFirst().orElseThrow(()->new IllegalArgumentException("No such feast."));}
    public boolean protectedRelations(CampaignState state,HouseId host,HouseId guest){
        Relation relation=state.diplomacy().between(host,guest);
        return relation.status()==Relation.Status.ALLIED || (relation.status()==Relation.Status.NEUTRAL && state.round()<=relation.truceUntil());
    }
    public String invite(CampaignState state,HouseId host,HouseId guest){
        opponent(state,host,guest);HouseState house=state.house(host);Territory castle=state.map().territory(house.army().location());
        require(castle.owner()==host&&castle.fort()>0&&!house.army().units().isEmpty(),"Host a feast at your army's own fortified castle.");
        require(house.reputation()>=35,"At least 35 reputation is needed for a credible invitation.");
        require(protectedRelations(state,host,guest)&&state.diplomacy().between(host,guest).trust()>=10,"Need an alliance or active truce and trust of at least 10.");
        require(state.feasts().stream().noneMatch(f->f.active()&&(f.host()==host||f.guest()==host||f.host()==guest||f.guest()==guest)),"A participant already has an active meeting.");
        pay(state,host,new Cost(2,2,2),1);
        Feast feast=new Feast(state.nextFeastId(),host,guest,castle.id(),state.round());state.feasts().add(feast);
        state.events().publish(new WorldNotice("invitation",state.round(),host,host.displayName()+" invites "+guest.displayName()+" to "+castle.name()+" in round "+feast.meetingRound()+"."));
        new edu.wotfk.story.FeastEventCreator().offer(state,guest);
        return "Feast #"+feast.id()+" scheduled for the end of round "+feast.meetingRound()+". Stay at "+castle.id()+"; your guest must accept and physically arrive.";
    }
    public String accept(CampaignState state,HouseId guest,int id){
        Feast feast=find(state,id);require(feast.guest()==guest&&feast.status()==Feast.Status.INVITED,"Only the invited guest can accept an open invitation.");
        require(state.round()<=feast.meetingRound()&&state.alive(feast.host())&&protectedRelations(state,feast.host(),guest),"The invitation is no longer politically valid.");
        pay(state,guest,new Cost(0,1,0),0);feast.accept();clearInvitation(state,guest,id);
        return "Accepted feast #"+id+" for 1 food. Move your army to "+feast.castle()+" by the end of round "+feast.meetingRound()+". Travel uses normal routes and costs.";
    }
    public String decline(CampaignState state,HouseId guest,int id){
        Feast feast=find(state,id);require(feast.guest()==guest&&feast.active(),"Only the guest can decline an active meeting.");
        pay(state,guest,Cost.NONE,0);feast.cancel();clearInvitation(state,guest,id);
        state.events().publish(new WorldNotice("feast-declined",state.round(),guest,guest.displayName()+" declines feast #"+id+"."));return "Meeting declined. No army has been moved.";
    }
    public String cancel(CampaignState state,HouseId host,int id){
        Feast feast=find(state,id);require(feast.host()==host&&feast.active(),"Only the host can cancel its active meeting.");
        pay(state,host,Cost.NONE,1);cancelMeeting(state,feast,"The host withdrew the invitation.");return "Meeting cancelled. Preparation costs are not refunded.";
    }
    public boolean canVisit(CampaignState state,HouseId guest,String destination){
        return state.feasts().stream().anyMatch(f->f.guest()==guest&&f.status()==Feast.Status.ACCEPTED&&f.castle().equals(destination)&&state.round()<=f.meetingRound()&&protectedRelations(state,f.host(),guest));
    }
    public boolean validMeeting(CampaignState state,Feast feast){
        return state.alive(feast.host())&&state.alive(feast.guest())&&feast.status()==Feast.Status.ACCEPTED
                &&state.map().territory(feast.castle()).owner()==feast.host()
                &&state.house(feast.host()).army().location().equals(feast.castle())
                &&state.house(feast.guest()).army().location().equals(feast.castle())
                &&!state.house(feast.guest()).army().units().isEmpty()&&protectedRelations(state,feast.host(),feast.guest());
    }
    public void resolveDue(CampaignState state){
        for(Feast feast:state.feasts())if(feast.active()&&state.round()>=feast.meetingRound()){
            if(!validMeeting(state,feast)){cancelMeeting(state,feast,"A participant failed to arrive, lost the castle, or broke the truce.");continue;}
            peaceful(state,feast);
        }
    }
    public void peaceful(CampaignState state,Feast feast){
        state.diplomacy().between(feast.host(),feast.guest()).changeTrust(20);
        for(HouseId id:new HouseId[]{feast.host(),feast.guest()}){state.house(id).resources().gain(0,0,2);state.house(id).army().changeMorale(8);state.house(id).changeReputation(3);}
        feast.complete();
        state.events().publish(new WorldNotice("guest-right",state.round(),feast.host(),"Feast #"+feast.id()+" honors guest right: both houses gain influence and morale; trust rises by 20."));
    }
    public void cancelMeeting(CampaignState state,Feast feast,String reason){
        feast.cancel();clearInvitation(state,feast.guest(),feast.id());
        state.events().publish(new WorldNotice("feast-cancelled",state.round(),feast.host(),"Feast #"+feast.id()+" cancelled. "+reason));
    }
    private void clearInvitation(CampaignState state,HouseId guest,int id){
        if(state.pendingEvents().get(guest) instanceof edu.wotfk.story.FeastInvitationEvent event&&event.feastId()==id)state.pendingEvents().remove(guest);
    }
    public String overview(CampaignState state,HouseId viewer){
        StringBuilder out=new StringBuilder("YOUR FEASTS\n");int count=0;
        for(Feast f:state.feasts())if(f.host()==viewer||f.guest()==viewer){
            count++;out.append("#").append(f.id()).append("  ").append(f.status()).append("  ").append(f.host()).append(" hosts ").append(f.guest()).append(" at ").append(f.castle()).append("  meeting round ").append(f.meetingRound()).append("\n");
            if(f.host()==viewer&&f.conspiracy()!=null)out.append("  Private plan: ").append(f.conspiracy().status()).append("\n");
            else if(state.knowledge().hasEvidence(viewer,f.id()))out.append("  Your scouts hold evidence of a conspiracy at this meeting.\n");
        }
        return count==0?"No invitations involve your house.":out.toString();
    }
}
