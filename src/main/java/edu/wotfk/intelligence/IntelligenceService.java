package edu.wotfk.intelligence;

import edu.wotfk.campaign.CampaignState;
import edu.wotfk.domain.*;
import java.util.*;
import static edu.wotfk.game.GameRules.*;
public final class IntelligenceService {
    public int chance(CampaignState state,HouseId actor,HouseId target){
        return Math.max(15,Math.min(95,60+state.house(actor).scout().intelligenceBonus()-(state.house(target).guarded(state.round())?30:0)));
    }
    public String spy(CampaignState state,HouseId actor,HouseId target){
        opponent(state,actor,target);int chance=chance(state,actor,target);
        pay(state,actor,new Cost(2,0,1),1);
        if(state.random().nextInt(100)<chance){
            HouseState observed=state.house(target);Map<String,Integer> garrisons=new LinkedHashMap<>();
            for(Territory territory:state.map().ownedBy(target))garrisons.put(territory.id(),territory.garrison());
            IntelReport report=new IntelReport(target,state.round(),observed.resources().gold(),observed.resources().food(),observed.resources().influence(),observed.army().units().size(),observed.army().morale(),observed.army().location(),garrisons);
            state.knowledge().remember(actor,report);
            return "Scout succeeded (chance "+chance+"%).\n"+report.describe(state.round())+new edu.wotfk.intrigue.ConspiracyService().discover(state,actor,target);
        }
        boolean detected=state.random().nextInt(100)<35;
        if(detected){state.house(actor).changeReputation(-3);state.diplomacy().between(actor,target).changeTrust(-8);}
        return "Scout failed (chance "+chance+"%). Resources are spent."+(detected?" The agents were detected: -3 reputation, -8 trust.":" No public evidence links the mission to your house.");
    }
    public String counterintelligence(CampaignState state,HouseId actor){
        require(!state.house(actor).guarded(state.round()),"Counterintelligence is already active.");
        pay(state,actor,new Cost(2,0,1),1);state.house(actor).guardUntil(state.round()+1);
        return "Counterintelligence active through round "+(state.round()+1)+". Enemy scouting is 30 percentage points less likely to succeed.";
    }
    public String report(CampaignState state,HouseId observer,HouseId target){
        require(observer!=target,"Use status or army for your own exact information.");
        IntelReport report=state.knowledge().report(observer,target);
        return report==null?"No report on "+target.displayName()+". Use spy HOUSE to gather a dated observation.":report.describe(state.round()) + "\nCollected conspiracy evidence IDs: " + state.knowledge().evidence(observer);
    }
    public String inspect(CampaignState state,HouseId observer,String id){
        Territory territory=state.map().territory(id);
        String text=territory.id()+"  "+territory.name()+"\nOwner: "+(territory.owner()==null?"Neutral regency":territory.owner().displayName())
                +"   Fort level: "+territory.fort()+"   Potential income: "+territory.goldYield()+" gold / "+territory.foodYield()+" food";
        if(territory.owner()==observer)return text+"\nYour garrison: "+territory.garrison()+". "+(territory.productive(state.round())?"Productive.":"Temporarily damaged.");
        if(territory.owner()==null)return text+"\nNeutral garrison: "+territory.garrison()+" (public regency muster).";
        IntelReport report=state.knowledge().report(observer,territory.owner());
        Integer garrison=report==null?null:report.garrisons().get(territory.id());
        return text+(garrison==null?"\nGarrison unknown. Send a scout to the owning house.":"\nObserved garrison: "+garrison+" in round "+report.observedRound()+"; it may have changed.");
    }
    private void opponent(CampaignState state, HouseId actor, HouseId target) {
        require(actor != null && target != null, "Choose a valid house.");
        require(actor != target, "You cannot target your own house.");
        require(state.alive(actor) && state.alive(target), "Both houses must be alive.");
    }
}
