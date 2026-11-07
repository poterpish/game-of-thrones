package edu.wotfk.houses;

import edu.wotfk.campaign.CampaignState;
import edu.wotfk.domain.*;
import edu.wotfk.events.WorldNotice;
import static edu.wotfk.game.GameRules.*;
/** Distinct faction opportunities with a shared three-round cooldown and explicit costs. */
public final class HouseAbilityService {
    public String use(CampaignState state,HouseId actor,String targetId){
        HouseState house=state.house(actor);require(state.round()>=house.nextAbilityRound(),"House ability returns in round "+house.nextAbilityRound()+".");
        Territory target=null;
        if(actor==HouseId.GREYJOY){
            require(targetId!=null,"Greyjoy uses special ID against an adjacent hostile coastal holding.");target=state.map().territory(targetId);
            require(state.map().coastal(target.id())&&state.map().route(house.army().location(),target.id()).isPresent(),"Choose an adjacent coastal holding.");
            require(!house.army().units().isEmpty(),"Raiding requires troops.");
            require(target.owner()!=actor&&(target.owner()==null||state.diplomacy().atWar(actor,target.owner())),"Declare war before raiding another house.");
        }else require(targetId==null,"This house uses special without a target ID.");
        pay(state,actor,new Cost(0,0,2),1);String message;
        switch(actor){
            case STARK->{house.resources().gain(0,4,0);house.army().changeMorale(15);message="Northern resolve: +4 food and up to +15 morale.";}
            case LANNISTER->{house.resources().gain(8,0,0);house.changeReputation(-3);message="Call in western debts: +8 gold, -3 reputation.";}
            case TARGARYEN->{house.army().supportUntil(state.round()+1);house.army().changeMorale(10);message="Rally the dragon banner: commander support through next round and +10 morale.";}
            case BARATHEON->{house.changeReputation(8);house.resources().gain(0,0,1);state.diplomacy().changeOthersTrust(actor,3);message="Royal proclamation: +8 reputation, +1 influence and +3 trust.";}
            case GREYJOY->{
                int gold=6,food=3;
                if(target.owner()!=null){Resources victim=state.house(target.owner()).resources();gold=Math.min(6,victim.gold());food=Math.min(3,victim.food());victim.pay(new Cost(gold,food,0));}
                house.resources().gain(gold,food,0);house.changeReputation(-5);state.diplomacy().changeOthersTrust(actor,-3);target.damageUntil(state.round()+1);
                message="Coastal raid on "+target.name()+": "+gold+" gold, "+food+" food; reputation -5, trust -3. Port income is lost for two settlements.";
            }
            default->throw new IllegalStateException("Unknown house.");
        }
        house.abilityUsed(state.round());state.events().publish(new WorldNotice("house-ability",state.round(),actor,actor.displayName()+": "+message));
        return message+" Ability available again in round "+house.nextAbilityRound()+".";
    }
}
