package edu.wotfk.combat;

import edu.wotfk.campaign.CampaignState;
import edu.wotfk.domain.*;
/** An empty army is not a mobile fighting force; relocate only its ruler so rebuilding stays possible. */
public final class ArmyRecoveryService {
    public void recover(CampaignState state,HouseId actor){
        Army army=state.house(actor).army();
        if(!army.units().isEmpty()||state.map().territory(army.location()).owner()==actor)return;
        state.map().ownedBy(actor).stream().findFirst().ifPresent(t->{army.moveTo(t.id());state.log(actor.displayName()+" regroups its unarmed household at "+t.name()+".");});
    }
}
