package edu.wotfk.intrigue;

import edu.wotfk.campaign.CampaignState;
import edu.wotfk.domain.*;
import edu.wotfk.diplomacy.DiplomacyService;
import edu.wotfk.story.*;
import static edu.wotfk.game.GameRules.*;
public final class ConspiracyService {
    public String prepare(CampaignState state,HouseId host,int id){
        Feast feast=new FeastService().find(state,id);
        require(feast.host()==host&&feast.status()==Feast.Status.ACCEPTED,"Prepare only your accepted feast.");
        require(feast.conspiracy()==null,"This meeting has already had its conspiracy preparation.");
        require(state.round()<feast.meetingRound(),"Preparation requires at least one round before the meeting.");
        require(state.map().territory(feast.castle()).owner()==host&&state.house(host).army().location().equals(feast.castle()),"Keep control of the meeting castle and remain there.");
        pay(state,host,new Cost(3,0,2),1);feast.prepareConspiracy(state.round());
        return "Secret preparations made for feast #"+id+". Spies can now discover evidence. Costs are sunk even if you abandon the plan.";
    }
    public String abandon(CampaignState state,HouseId host,int id){
        Feast feast=new FeastService().find(state,id);
        require(feast.host()==host&&feast.active()&&feast.conspiracy()!=null&&feast.conspiracy().prepared(),"No prepared conspiracy belongs to you here.");
        pay(state,host,Cost.NONE,1);feast.conspiracy().finish(Conspiracy.Status.ABANDONED);
        return "The conspiracy is abandoned. The feast can honor guest right, but preparation resources are not refunded.";
    }
    public int ambushChance(CampaignState state,Feast feast){
        int chance=70+state.house(feast.host()).scout().intelligenceBonus()-state.house(feast.guest()).scout().intelligenceBonus();
        if(state.house(feast.guest()).guarded(state.round()))chance-=30;
        return Math.max(15,Math.min(90,chance));
    }
    public String resolveAtMeeting(CampaignState state,Feast feast){
        require(!state.finished(),"The campaign has ended.");
        require(new FeastService().validMeeting(state,feast),"The meeting prerequisites no longer hold.");
        require(feast.conspiracy()!=null&&feast.conspiracy().prepared()&&feast.conspiracy().preparedRound()<state.round(),"A prepared conspiracy needs time before execution.");
        Army guest=state.house(feast.guest()).army();Army host=state.house(feast.host()).army();
        int chance=ambushChance(state,feast);boolean success=state.random().nextInt(100)<chance;int losses=0;
        if(success){
            losses=guest.lose(Math.min(Math.max(0,guest.units().size()-1),guest.units().size()/2));
            guest.changeMorale(-35);host.changeMorale(4);feast.conspiracy().finish(Conspiracy.Status.EXECUTED);
        }else{
            host.lose(1);host.changeMorale(-15);guest.changeMorale(-8);feast.conspiracy().finish(Conspiracy.Status.FAILED);
        }
        new DiplomacyService().recordBetrayal(state,feast.host(),feast.guest());
        feast.complete();state.storyFlags().add("red-feast:"+feast.host());state.storyFlags().add("feast-victim:"+feast.guest());
        new ConsequenceScheduler().schedule(state,feast.host(),2,ScheduledConsequence.Kind.BACKLASH,8,"Vassals recoil from the violation of guest right; reputation -5 and trust -8.");
        String retreat=new edu.wotfk.combat.RetreatService().withdraw(state,feast.guest(),feast.castle());
        state.sieges().remove(feast.guest());
        String result=success?"The red feast succeeds: "+feast.guest().displayName()+" loses "+losses+" detachments and 35 morale.":"The ambush fails: the host loses one detachment and 15 morale; the guest loses 8 morale.";
        result+=" "+retreat+" No territory changes owner automatically. Political backlash is due in round "+(state.round()+2)+".";
        state.events().publish(new edu.wotfk.events.WorldNotice("red-feast",state.round(),feast.host(),result));
        return result;
    }
    public String discover(CampaignState state,HouseId observer,HouseId target){
        StringBuilder result=new StringBuilder();
        for(Feast feast:state.feasts())if(feast.host()==target&&feast.active()&&feast.conspiracy()!=null&&feast.conspiracy().prepared()){
            state.knowledge().recordEvidence(observer,feast.id());
            result.append("\nEVIDENCE: bribed guards at feast #").append(feast.id()).append(". Use expose ").append(feast.id()).append(" to stop the meeting, or warn your own court by refusing to attend.");
        }
        return result.toString();
    }
    public String expose(CampaignState state,HouseId actor,int id){
        Feast feast=new FeastService().find(state,id);
        require(actor!=feast.host()&&state.knowledge().hasEvidence(actor,id),"Exposure requires evidence from a successful spy mission.");
        require(feast.active()&&feast.conspiracy()!=null&&feast.conspiracy().prepared(),"No active conspiracy remains to expose.");
        pay(state,actor,new Cost(0,0,1),1);feast.conspiracy().finish(Conspiracy.Status.EXPOSED);
        new FeastService().cancelMeeting(state,feast,"Evidence of bribed guards is made public.");
        new DiplomacyService().recordBetrayal(state,feast.host(),feast.guest());state.house(actor).changeReputation(6);
        new ConsequenceScheduler().schedule(state,feast.host(),2,ScheduledConsequence.Kind.BACKLASH,8,"Vassals demand reassurance after the exposed conspiracy; reputation -5 and trust -8.");
        String retreat="";
        if(state.house(feast.guest()).army().location().equals(feast.castle()))retreat=" "+new edu.wotfk.combat.RetreatService().withdraw(state,feast.guest(),feast.castle());
        return "Conspiracy exposed. The ambush is prevented; its organizer still suffers diplomatic penalties."+retreat;
    }
}
