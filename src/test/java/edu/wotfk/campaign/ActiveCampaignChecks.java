package edu.wotfk.campaign;

import edu.wotfk.domain.*;
import edu.wotfk.game.GameFacade;
import static edu.wotfk.testsupport.TestSupport.*;

/** Exercises actual player commands as well as the ordinary opponent coordinator. */
public final class ActiveCampaignChecks {
    public static void main(String[] args) {
        for (HouseId player : HouseId.values()) for (int seed=20;seed<25;seed++) {
            CampaignState state=new CampaignDirector().standard(player,seed);
            GameFacade game=new GameFacade(state);
            while (!state.finished()) {
                HouseState own=state.house(player);
                if (state.pendingEvents().containsKey(player)) {
                    var event=state.pendingEvents().get(player);
                    game.choose(event.choices().size());
                }
                if (own.resources().food()<8) game.supply();
                if (own.army().units().size()<4) game.recruit(UnitType.MILITIA);
                boolean attacked=false;
                for (WorldMap.Route route:state.map().routes(own.army().location())) {
                    Territory destination=state.map().territory(route.destination());
                    if (destination.owner()==null || state.diplomacy().atWar(player,destination.owner())) {
                        game.attack(destination.id(),"cautious"); attacked=true; break;
                    }
                }
                if (!attacked && own.actions()>0) {
                    for (WorldMap.Route route:state.map().routes(own.army().location())) {
                        if (state.map().territory(route.destination()).owner()==player) {game.move(route.destination());break;}
                    }
                }
                game.endTurn();
                if (state.round()>12) throw new AssertionError("Campaign did not terminate");
                for (HouseState h:state.houses()) if(h.resources().gold()<0||h.resources().food()<0||h.actions()<0||h.actions()>3)throw new AssertionError("Invalid live state");
            }
            check(state.finished(),"Active player campaign completed: "+player+" / seed "+seed);
        }
        System.out.println("SIMULATION: 25 active-player campaigns completed.");
    }
}
