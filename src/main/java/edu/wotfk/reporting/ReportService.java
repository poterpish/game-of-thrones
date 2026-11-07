package edu.wotfk.reporting;

import edu.wotfk.campaign.CampaignState;
import edu.wotfk.domain.HouseId;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.io.IOException;
import java.util.Locale;
public final class ReportService {
    public String render(CampaignState state,HouseId viewer,String type,String format){
        ReportRenderer renderer=switch(format.toLowerCase(Locale.ROOT)){case "console"->new ConsoleRenderer();case "markdown"->new MarkdownRenderer();default->throw new IllegalArgumentException("Format must be console or markdown.");};
        Report report=switch(type.toLowerCase(Locale.ROOT)){case "economy"->new EconomyReport(renderer);case "military"->new MilitaryReport(renderer);default->throw new IllegalArgumentException("Report must be economy or military.");};
        return report.render(state,viewer);
    }
    public String export(CampaignState state,HouseId viewer,String type){
        String content=render(state,viewer,type,"markdown");
        Path file=Path.of("reports",type.toLowerCase(Locale.ROOT)+"-"+viewer.name().toLowerCase(Locale.ROOT)+"-round"+state.round()+".md");
        try{Files.createDirectories(file.getParent());Files.writeString(file,content+System.lineSeparator(),StandardCharsets.UTF_8);}
        catch(IOException error){throw new IllegalStateException("Cannot write report: "+error.getMessage(),error);}
        return "Report written to "+file.toAbsolutePath()+". Exporting does not spend an action.";
    }
}
