package edu.wotfk.campaign;

import edu.wotfk.domain.*;
import static edu.wotfk.testsupport.TestSupport.*;
public final class SimulationChecks {
    public static void main(String[] args){
        int finished=0;
        for(HouseId player:HouseId.values())for(int seed=0;seed<10;seed++){
            CampaignState s=new CampaignDirector().standard(player,seed);int rounds=0;
            while(!s.finished()&&rounds++<12){new TurnService().endRound(s);verify(s);}
            check(s.finished(),"Bounded campaign: "+player+" / seed "+seed);finished++;
        }
        System.out.println("SIMULATION: "+finished+" complete passive-player campaigns.");
    }
    private static void verify(CampaignState s){
        if(s.map().territories().size()!=12)throw new AssertionError("Map size changed");
        for(HouseState house:s.houses()){
            if(house.resources().gold()<0||house.resources().food()<0||house.resources().influence()<0)throw new AssertionError("Negative balance");
            if(house.actions()<0||house.actions()>3||house.army().units().size()>6||house.army().morale()<0||house.army().morale()>100)throw new AssertionError("House invariant broken");
            s.map().territory(house.army().location());
            if(house.army().units().stream().anyMatch(u->u.house()!=house.id()))throw new AssertionError("Mixed factory families");
        }
        if(!s.events().failures().isEmpty())throw new AssertionError("Unexpected observer failure");
    }
}
