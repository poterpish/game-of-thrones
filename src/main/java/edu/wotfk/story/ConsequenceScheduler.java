package edu.wotfk.story;

import edu.wotfk.campaign.CampaignState;
import edu.wotfk.domain.*;
import edu.wotfk.events.WorldNotice;
import java.util.*;
public final class ConsequenceScheduler {
    public void schedule(CampaignState state,HouseId house,int delay,ScheduledConsequence.Kind kind,int amount,String reason){
        if(delay<1)throw new IllegalArgumentException("Consequences must be delayed by at least one round.");
        state.consequences().add(new ScheduledConsequence(state.nextConsequenceId(),state.round()+delay,house,kind,amount,reason));
    }
    public void applyDue(CampaignState state){
        for(ScheduledConsequence c:List.copyOf(state.consequences())){
            if(c.dueRound()>state.round())continue;
            state.consequences().remove(c); // Consume once, including consequences for eliminated houses.
            if(!state.alive(c.house()))continue;
            HouseState house=state.house(c.house());String result=c.reason();
            switch(c.kind()){
                case DEBT -> {
                    boolean paid=house.resources().gold()>=c.amount();
                    house.resources().pay(new Cost(Math.min(house.resources().gold(),c.amount()),0,0));
                    if(!paid){house.changeReputation(-20);house.resources().pay(new Cost(0,0,Math.min(3,house.resources().influence())));}
                    result=paid?"The crown loan is repaid in full.":"The crown loan defaults: remaining gold is seized, reputation -20, influence -3 (bounded by available influence).";
                }
                case FOOD -> house.resources().gain(0,c.amount(),0);
                case GOLD -> house.resources().gain(c.amount(),0,0);
                case MORALE -> house.army().changeMorale(c.amount());
                case TRUST -> state.diplomacy().changeOthersTrust(c.house(),c.amount());
                case LEVY_REACTION -> {house.changeReputation(-8);house.army().changeMorale(-8);}
                case BACKLASH -> {house.changeReputation(-5);state.diplomacy().changeOthersTrust(c.house(),-c.amount());}
            }
            state.events().publish(new WorldNotice("consequence",state.round(),c.house(),c.house().displayName()+": "+result));
        }
    }
    public String overview(CampaignState state,HouseId viewer){
        StringBuilder out=new StringBuilder("SCHEDULED CONSEQUENCES\n");int count=0;
        for(ScheduledConsequence c:state.consequences())if(c.house()==viewer){count++;out.append("Round ").append(c.dueRound()).append("  ").append(c.kind()).append("  ").append(c.amount()).append("  ").append(c.reason()).append("\n");}
        return count==0?"No scheduled consequences for your house.":out.toString();
    }
}
