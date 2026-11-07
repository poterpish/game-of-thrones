package edu.wotfk.ai;

import edu.wotfk.campaign.CampaignState;
import edu.wotfk.domain.*;
import edu.wotfk.diplomacy.*;
import edu.wotfk.intrigue.*;
import edu.wotfk.intelligence.*;
import edu.wotfk.houses.HouseAbilityService;
import java.util.*;
/** One political decision per turn, based on public facts, own plans and acquired evidence. */
public final class PoliticalAi {
    private final DiplomacyService diplomacy=new DiplomacyService();
    public void act(CampaignState state,HouseId actor){
        HouseState house=state.house(actor);if(house.actions()==0)return;
        try{
            for(Feast f:state.feasts())if(f.host()!=actor&&f.active()&&state.knowledge().hasEvidence(actor,f.id())){
                new ConspiracyService().expose(state,actor,f.id());return;
            }
            Feast meeting=state.feasts().stream().filter(f->f.active()&&(f.host()==actor||f.guest()==actor)).findFirst().orElse(null);
            if(meeting!=null){
                if(meeting.host()==actor&&meeting.status()==Feast.Status.ACCEPTED&&meeting.conspiracy()==null&&state.round()<meeting.meetingRound()
                        &&(actor==HouseId.LANNISTER||actor==HouseId.GREYJOY||(actor==HouseId.TARGARYEN&&house.reputation()<45))
                        &&house.reputation()<=55&&state.diplomacy().between(actor,meeting.guest()).trust()<60){
                    new ConspiracyService().prepare(state,actor,meeting.id());return;
                }
                if(meeting.guest()==actor&&state.house(meeting.host()).reputation()<60){
                    IntelReport report=state.knowledge().report(actor,meeting.host());
                    if(report==null||report.observedRound()<state.round()-1){new IntelligenceService().spy(state,actor,meeting.host());return;}
                    if(!house.guarded(state.round())){new IntelligenceService().counterintelligence(state,actor);return;}
                }
                return;
            }
            for(HouseId other:HouseId.values())if(other!=actor&&state.alive(other)&&state.diplomacy().allied(actor,other)){
                Relation r=state.diplomacy().between(actor,other);
                if(r.trust()<0||state.house(other).reputation()<25){diplomacy.breakAlliance(state,actor,other);return;}
                if(other!=state.player()&&state.map().territory("KL").owner()==actor&&house.resources().influence()>=13
                        &&!state.diplomacy().endorsements().containsKey(other)&&diplomacy.allianceScore(state,actor,other,0)>=55){
                    diplomacy.requestEndorsement(state,actor,other);return;
                }
                boolean atWar=Arrays.stream(HouseId.values()).anyMatch(h->h!=actor&&state.alive(h)&&state.diplomacy().atWar(actor,h));
                if(atWar&&(house.resources().gold()<5||house.resources().food()<6)&&state.round()>=state.diplomacy().aidCooldown().getOrDefault(actor,0)&&!state.diplomacy().aidRequests().containsKey(other)){
                    diplomacy.requestAid(state,actor,other);return;
                }
            }
            if(state.round()>=3&&state.round()<=8&&state.round()%3==actor.ordinal()%3
                    &&house.reputation()>=35&&state.map().territory(house.army().location()).owner()==actor){
                for(HouseId guest:HouseId.values())if(guest!=actor&&state.alive(guest)&&state.diplomacy().allied(actor,guest)
                        &&state.diplomacy().between(actor,guest).trust()>=20
                        &&state.map().routes(house.army().location()).stream().anyMatch(r->state.map().territory(r.destination()).owner()==guest)
                        &&state.feasts().stream().noneMatch(f->f.active()&&(f.host()==guest||f.guest()==guest))){
                    new FeastService().invite(state,actor,guest);return;
                }
            }
            if(state.round()>=2&&state.round()%2==actor.ordinal()%2){
                for(HouseId other:HouseId.values())if(other!=actor&&other!=state.player()&&state.alive(other)
                        &&state.diplomacy().between(actor,other).status()==Relation.Status.NEUTRAL
                        &&diplomacy.allianceScore(state,actor,other,0)>=45){
                    diplomacy.alliance(state,actor,other,0);return;
                }
            }
            if(house.resources().influence()>=2&&state.round()>=house.nextAbilityRound()){
                boolean useful=switch(actor){
                    case STARK->house.resources().food()<12||house.army().morale()<65;
                    case LANNISTER->house.resources().gold()<10;
                    case TARGARYEN->!house.army().commanded(state.round())&&house.army().units().size()>=3;
                    case BARATHEON->house.reputation()<80;
                    case GREYJOY->false;
                };
                if(useful){new HouseAbilityService().use(state,actor,null);return;}
                if(actor==HouseId.GREYJOY)for(WorldMap.Route r:state.map().routes(house.army().location())){
                    Territory target=state.map().territory(r.destination());
                    if(state.map().coastal(target.id())&&target.owner()!=actor&&(target.owner()==null||state.diplomacy().atWar(actor,target.owner()))){new HouseAbilityService().use(state,actor,target.id());return;}
                }
            }
            if(state.round()%3==0&&house.resources().gold()>=5&&house.resources().influence()>=2){
                for(WorldMap.Route route:state.map().routes(house.army().location())){
                    HouseId target=state.map().territory(route.destination()).owner();
                    if(target!=null&&target!=actor&&!state.diplomacy().allied(actor,target)){
                        IntelReport report=state.knowledge().report(actor,target);
                        if(report==null||state.round()-report.observedRound()>=3){new IntelligenceService().spy(state,actor,target);return;}
                    }
                }
            }
        }catch(IllegalStateException unavailable){
            // Political opportunities may be unaffordable. Ordinary army priorities can still proceed.
        }
    }
}
