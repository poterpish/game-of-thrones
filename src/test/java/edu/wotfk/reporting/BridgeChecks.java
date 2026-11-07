package edu.wotfk.reporting;

import edu.wotfk.campaign.*;
import edu.wotfk.domain.*;
import edu.wotfk.economy.*;
import edu.wotfk.game.GameFacade;
import static edu.wotfk.testsupport.TestSupport.*;
public final class BridgeChecks {
    public static void main(String[] args){
        CampaignState a=new CampaignDirector().standard(HouseId.STARK,9);CampaignState b=new CampaignDirector().standard(HouseId.STARK,9);
        GameFacade game=new GameFacade(a);String before=game.status();
        for(String type:new String[]{"economy","military"})for(String format:new String[]{"console","markdown"}){
            String output=game.report(type,format);
            check(output.toLowerCase(java.util.Locale.ROOT).contains(type+" report"),type+" renders as "+format);
            check(output.contains("Stark"),"Renderer preserves report identity: "+type+"/"+format);
            if(format.equals("markdown"))check(output.startsWith("# "),"Markdown renderer supplies its own syntax");
            else check(!output.startsWith("# "),"Console renderer uses minimal plain text");
        }
        equal(before,game.status(),"Reports cannot change balances, AP or armies");
        equal(a.random().nextInt(),b.random().nextInt(),"Reports consume no random numbers");
        check(game.report("military","console").contains("Lannister: no intelligence"),"Military reports respect fog of war");
        rejects(()->game.report("unknown","console"),"Unknown report type rejected");
        AssetSummaryVisitor visitor=new AssetSummaryVisitor(HouseId.STARK,1);
        for(Territory t:a.map().territories())t.accept(visitor);for(HouseState h:a.houses())h.army().accept(visitor);
        equal(2,visitor.territories(),"Second Visitor counts owned territories");equal(3,visitor.detachments(),"Second Visitor counts army elements");
    }
}
