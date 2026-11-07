package edu.wotfk.reporting;

import edu.wotfk.campaign.CampaignState;
import edu.wotfk.domain.*;
import edu.wotfk.economy.*;
import java.util.*;
public final class EconomyReport extends Report {
    public EconomyReport(ReportRenderer renderer){super(renderer);}
    @Override protected String title(){return "Economy report";}
    @Override protected void content(CampaignState state,HouseId viewer){
        HouseState house=state.house(viewer);EconomyLedger ledger=new EconomyService().forecast(state,viewer);
        renderer.paragraph(viewer.displayName()+" - round "+state.round()+". "+house.resources().summary());
        renderer.table(List.of("Resource","Income","Upkeep","Net"),List.of(
            List.of("Gold",""+ledger.goldIncome(),""+ledger.goldUpkeep(),""+(ledger.goldIncome()-ledger.goldUpkeep())),
            List.of("Food",""+ledger.foodIncome(),""+ledger.foodUpkeep(),""+(ledger.foodIncome()-ledger.foodUpkeep()))));
        renderer.paragraph("Forecast uses current ownership, active blockades, allied trade and troop effects. Opponents may change it before settlement. Each living house also gains 1 influence per round.");
        renderer.paragraph("Winter adds 1 food upkeep to each non-empty army from round 7. Shortfalls cause morale loss; food shortages also remove one detachment.");
        renderer.paragraph(new edu.wotfk.story.ConsequenceScheduler().overview(state,viewer));
    }
}
