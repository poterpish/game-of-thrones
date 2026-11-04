package edu.wotfk.economy;

import edu.wotfk.campaign.*;
import edu.wotfk.domain.*;
import static edu.wotfk.testsupport.TestSupport.*;
public final class EconomyChecks {
    public static void main(String[] args) {
        CampaignState state = new CampaignDirector().standard(HouseId.STARK, 2);
        EconomyService service = new EconomyService();
        EconomyLedger first = service.forecast(state, HouseId.STARK);
        equal(5, first.goldIncome(), "Visitor sums owned territory gold");
        equal(7, first.foodIncome(), "Visitor sums owned territory food");
        equal(1, first.goldUpkeep(), "Visitor visits guard upkeep");
        equal(4, first.foodUpkeep(), "Visitor visits three troop detachments");
        equal(first, service.forecast(state, HouseId.STARK), "Repeated forecast is pure");
        equal(12, state.house(HouseId.STARK).resources().gold(), "Forecast does not spend resources");
        new TurnService().endRound(state);
        equal(16, state.house(HouseId.STARK).resources().gold(), "Round settlement applies income minus upkeep once");
        equal(2, state.round(), "One end command advances one round");
        state.map().territory("TW").capture(HouseId.STARK);
        equal(8, service.forecast(state, HouseId.STARK).goldIncome(), "Changed ownership changes income");
        state.map().territory("TW").damageUntil(3);
        equal(5, service.forecast(state, HouseId.STARK).goldIncome(), "Damaged territory produces nothing");
        while (!state.finished()) new TurnService().endRound(state);
        int balance = state.house(HouseId.STARK).resources().gold();
        new TurnService().endRound(state);
        equal(balance, state.house(HouseId.STARK).resources().gold(), "Finished rounds cannot settle again");
    }
}
