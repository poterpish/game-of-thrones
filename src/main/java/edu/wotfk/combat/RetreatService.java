package edu.wotfk.combat;

import edu.wotfk.campaign.CampaignState;
import edu.wotfk.domain.*;
import java.util.Comparator;
/** A retreat uses one real adjacent route; trapped survivors surrender instead of teleporting. */
public final class RetreatService {
    public String withdraw(CampaignState state,HouseId actor,String from){
        Army army=state.house(actor).army();
        String destination=state.map().routes(from).stream().map(WorldMap.Route::destination)
                .filter(id->{HouseId owner=state.map().territory(id).owner();return owner==actor||owner==null||state.diplomacy().allied(actor,owner);})
                .sorted(Comparator.comparingInt(id->state.map().territory(id).owner()==actor?0:1)).findFirst().orElse(null);
        if(destination!=null){army.moveTo(destination);return "Survivors withdraw along the map to "+destination+".";}
        army.lose(army.units().size());state.map().ownedBy(actor).stream().findFirst().ifPresent(t->army.moveTo(t.id()));
        return "No safe adjacent retreat exists: the troops surrender. The ruler can rebuild at a remaining holding.";
    }
}
