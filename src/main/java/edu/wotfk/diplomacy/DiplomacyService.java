package edu.wotfk.diplomacy;

import edu.wotfk.campaign.CampaignState;
import edu.wotfk.domain.*;
import static edu.wotfk.game.GameRules.*;
public final class DiplomacyService {
    public String declareWar(CampaignState state, HouseId actor, HouseId target) {
        opponent(state, actor, target); Relation relation = state.diplomacy().between(actor, target);
        require(relation.status() != Relation.Status.WAR, "War has already been declared.");
        require(relation.status() != Relation.Status.ALLIED && state.round() > relation.truceUntil(), "An alliance or truce protects this house. Use an explicit betrayal.");
        pay(state, actor, new Cost(0, 0, 2), 1);
        relation.status(Relation.Status.WAR); relation.changeTrust(-20);
        state.log(actor.displayName() + " declares war on " + target.displayName() + ".");
        return "War declared on " + target.displayName() + ".";
    }
    public int allianceScore(CampaignState state, HouseId actor, HouseId target, int gift) {
        return state.diplomacy().between(actor,target).trust() + state.house(actor).reputation()/2
                + state.house(actor).diplomat().negotiationBonus() + gift*3
                - (state.map().territory("KL").owner() == target ? 20 : 0);
    }
    private void giftAmount(int gift) { require(gift >= 0 && gift <= 20, "A diplomatic gift must be 0..20 gold."); }
    public String alliance(CampaignState state, HouseId actor, HouseId target, int gift) {
        opponent(state, actor, target); giftAmount(gift); Relation r = state.diplomacy().between(actor,target);
        require(r.status() == Relation.Status.NEUTRAL, "Make peace before an alliance; existing allies need no second treaty.");
        int score = allianceScore(state,actor,target,gift);
        pay(state,actor,new Cost(gift,0,2),1); state.house(target).resources().gain(gift,0,0);
        if (score < 45) { r.changeTrust(3); return "Alliance refused (score " + score + "/45). The gift was delivered; trust rises by 3."; }
        r.status(Relation.Status.ALLIED); r.changeTrust(15); state.house(actor).changeReputation(2);
        notice(state,actor,"alliance",actor.displayName()+" and "+target.displayName()+" sign an alliance.");
        return "Alliance accepted (score " + score + "/45). Passage, +1 gold/round and mutual aid obligations are active.";
    }
    public String peace(CampaignState state, HouseId actor, HouseId target, int gift) {
        opponent(state,actor,target); giftAmount(gift); Relation r=state.diplomacy().between(actor,target);
        require(r.status()==Relation.Status.WAR,"You are not at war with that house.");
        int score=r.trust()+state.house(actor).reputation()/2+state.house(actor).diplomat().negotiationBonus()+gift*4
                +5*(state.map().ownedBy(actor).size()-state.map().ownedBy(target).size());
        pay(state,actor,new Cost(gift,0,2),1);state.house(target).resources().gain(gift,0,0);
        if(score<20){r.changeTrust(2);return "Peace refused (score "+score+"/20). Envoys and gifts are spent.";}
        r.status(Relation.Status.NEUTRAL);r.truceUntil(state.round()+2);r.changeTrust(10);
        state.sieges().values().removeIf(s -> !new edu.wotfk.combat.SiegeService().valid(state,s));
        notice(state,actor,"peace",actor.displayName()+" and "+target.displayName()+" agree a truce through round "+r.truceUntil()+".");
        return "Peace accepted. Attacking during the truce is a betrayal.";
    }
    public String breakAlliance(CampaignState state,HouseId actor,HouseId target){
        opponent(state,actor,target);Relation r=state.diplomacy().between(actor,target);
        require(r.status()==Relation.Status.ALLIED,"There is no alliance to break.");pay(state,actor,Cost.NONE,1);
        r.status(Relation.Status.NEUTRAL);r.truceUntil(state.round()+2);r.changeTrust(-20);state.house(actor).changeReputation(-8);
        state.diplomacy().revokeBetween(actor,target);
        notice(state,actor,"treaty-broken",actor.displayName()+" ends its alliance with "+target.displayName()+". A two-round truce remains.");
        return "Alliance ended; -8 reputation, -20 trust, and no ordinary attack before round "+(r.truceUntil()+1)+".";
    }
    public String betray(CampaignState state,HouseId actor,HouseId target){
        opponent(state,actor,target);Relation r=state.diplomacy().between(actor,target);
        require(r.status()==Relation.Status.ALLIED || (r.status()!=Relation.Status.WAR && state.round()<=r.truceUntil()),"There is no protected agreement to betray; use war instead.");
        pay(state,actor,new Cost(0,0,2),1);recordBetrayal(state,actor,target);
        return "Betrayal declared. War begins; reputation falls by 30, trust falls across the realm and your endorsements are revoked.";
    }
    /** Used after a committed conspiracy as well as an explicit diplomatic betrayal. */
    public void recordBetrayal(CampaignState state,HouseId actor,HouseId victim){
        Relation r=state.diplomacy().between(actor,victim);r.status(Relation.Status.WAR);r.changeTrust(-60);
        state.house(actor).changeReputation(-30);state.diplomacy().changeOthersTrust(actor,-12);
        state.diplomacy().endorsements().entrySet().removeIf(e->e.getValue()==actor || e.getKey()==actor);
        state.storyFlags().add("betrayed:"+actor);
        notice(state,actor,"betrayal",actor.displayName()+" betrays "+victim.displayName()+". Other houses reconsider its promises.");
    }
    public String requestEndorsement(CampaignState state,HouseId actor,HouseId target){
        opponent(state,actor,target);require(state.diplomacy().allied(actor,target),"Only an ally can endorse your claim.");
        require(!state.diplomacy().endorsements().containsKey(target),"That house has already endorsed a claimant.");
        int score=allianceScore(state,actor,target,0);
        pay(state,actor,new Cost(5,0,3),1);state.house(target).resources().gain(5,0,0);
        if(score<55)return "Endorsement refused (score "+score+"/55). Negotiation resources are spent.";
        state.diplomacy().endorsements().put(target,actor);
        notice(state,actor,"endorsement",target.displayName()+" publicly endorses "+actor.displayName()+" for the throne.");
        return "Endorsement secured. Each house can endorse one claimant; treaty violations revoke support.";
    }
    public String requestAid(CampaignState state,HouseId actor,HouseId target){
        opponent(state,actor,target);require(state.diplomacy().allied(actor,target),"Request aid from an ally.");
        require(java.util.Arrays.stream(HouseId.values()).anyMatch(h->h!=actor&&state.alive(h)&&state.diplomacy().atWar(actor,h)),"Request military aid only while at war.");
        require(!state.diplomacy().aidRequests().containsKey(target),"That ally already has an unanswered request.");
        require(state.round()>=state.diplomacy().aidCooldown().getOrDefault(actor,0),"You may request aid only once every three rounds.");
        pay(state,actor,Cost.NONE,1);
        state.diplomacy().aidRequests().put(target,new AidRequest(actor,target,state.round()+1));state.diplomacy().aidCooldown().put(actor,state.round()+3);
        notice(state,actor,"aid-request",actor.displayName()+" requests 4 gold and 2 food from "+target.displayName()+" by round "+(state.round()+1)+".");
        return "Aid requested; ignoring the deadline costs your ally trust and reputation.";
    }
    public String giveAid(CampaignState state,HouseId actor,HouseId target){
        opponent(state,actor,target);require(state.diplomacy().allied(actor,target),"Aid is reserved for allies.");
        pay(state,actor,new Cost(4,2,0),1);state.house(target).resources().gain(4,2,0);
        state.diplomacy().between(actor,target).changeTrust(12);state.house(actor).changeReputation(3);
        AidRequest request=state.diplomacy().aidRequests().get(actor);
        if(request!=null&&request.requester()==target)state.diplomacy().aidRequests().remove(actor);
        notice(state,actor,"aid",actor.displayName()+" sends supplies to "+target.displayName()+".");return "Sent 4 gold and 2 food; +12 trust, +3 reputation.";
    }
    public void answerAiRequest(CampaignState state,HouseId actor){
        AidRequest request=state.diplomacy().aidRequests().get(actor);
        if(request!=null&&state.alive(actor)&&state.alive(request.requester())&&state.diplomacy().allied(actor,request.requester())&&state.house(actor).actions()>0&&state.house(actor).resources().canPay(new Cost(4,2,0)))giveAid(state,actor,request.requester());
    }
    public void expireRequests(CampaignState state){
        var iterator=state.diplomacy().aidRequests().entrySet().iterator();
        while(iterator.hasNext()){
            AidRequest request=iterator.next().getValue();
            if(!state.alive(request.requester())||!state.alive(request.recipient())||!state.diplomacy().allied(request.requester(),request.recipient())){iterator.remove();continue;}
            if(state.round()<request.dueRound())continue;
            Relation r=state.diplomacy().between(request.requester(),request.recipient());r.changeTrust(-20);state.house(request.recipient()).changeReputation(-8);
            if(r.trust()<0){r.status(Relation.Status.NEUTRAL);r.truceUntil(state.round()+2);state.diplomacy().revokeBetween(request.requester(),request.recipient());}
            notice(state,request.recipient(),"aid-refused",request.recipient().displayName()+" fails to honor an aid obligation.");iterator.remove();
        }
    }
    private void notice(CampaignState state,HouseId actor,String type,String message){state.events().publish(new edu.wotfk.events.WorldNotice(type,state.round(),actor,message));}
    public String overview(CampaignState state, HouseId actor) {
        StringBuilder text = new StringBuilder("HOUSE       STATUS    TRUST   TRUCE THROUGH\n");
        for (HouseId other : HouseId.values()) if (other != actor && state.alive(other)) {
            Relation r = state.diplomacy().between(actor, other);
            text.append(String.format("%-11s %-9s %5d   %d%n", other.displayName(), r.status(), r.trust(), r.truceUntil()));
        }
        text.append("Endorsements: ").append(state.diplomacy().endorsements()).append("\n");
        AidRequest request=state.diplomacy().aidRequests().get(actor);
        if(request!=null)text.append("OBLIGATION: aid ").append(request.requester()).append(" by round ").append(request.dueRound()).append(" (4 gold, 2 food, 1 AP).\n");
        return text.toString();
    }

    private void opponent(CampaignState state, HouseId actor, HouseId target) {
        require(actor != null && target != null,
                "Choose a valid house.");
        require(actor != target,
                "You cannot negotiate with your own house.");
        require(state.alive(actor) && state.alive(target),
                "Both houses must be alive.");
    }

}
