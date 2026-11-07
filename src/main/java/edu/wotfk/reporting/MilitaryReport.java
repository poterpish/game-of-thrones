package edu.wotfk.reporting;

import edu.wotfk.campaign.CampaignState;
import edu.wotfk.domain.*;
import edu.wotfk.economy.AssetSummaryVisitor;
import java.util.*;
public final class MilitaryReport extends Report {
    public MilitaryReport(ReportRenderer renderer){super(renderer);}
    @Override protected String title(){return "Military report";}
    @Override protected void content(CampaignState state,HouseId viewer){
        HouseState house=state.house(viewer);AssetSummaryVisitor visitor=new AssetSummaryVisitor(viewer,state.round());
        for(Territory territory:state.map().territories())territory.accept(visitor);
        for(HouseState h:state.houses())h.army().accept(visitor);
        renderer.paragraph(viewer.displayName()+" at "+house.army().location()+", morale "+house.army().morale()+". "+visitor.summary());
        List<List<String>> rows=new ArrayList<>();int index=0;
        for(Unit unit:house.army().effectiveUnits(state.round()))rows.add(List.of(""+(++index),unit.type().name(),""+unit.attack(),""+unit.defense(),""+unit.coastalBonus(),unit.name()));
        renderer.table(List.of("No","Type","Attack","Defense","Coast bonus","Effects"),rows);
        for(HouseId target:HouseId.values())if(target!=viewer){
            edu.wotfk.intelligence.IntelReport report=state.knowledge().report(viewer,target);
            renderer.paragraph(report==null?target.displayName()+": no intelligence report.":report.describe(state.round()));
        }
    }
}
