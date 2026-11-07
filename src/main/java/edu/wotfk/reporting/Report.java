package edu.wotfk.reporting;

import edu.wotfk.campaign.CampaignState;
import edu.wotfk.domain.HouseId;
/** Bridge abstraction axis: what to report. It owns an independently supplied renderer. */
public abstract class Report {
    protected final ReportRenderer renderer;
    protected Report(ReportRenderer renderer){this.renderer=java.util.Objects.requireNonNull(renderer);}
    protected abstract String title();
    protected abstract void content(CampaignState state,HouseId viewer);
    public final String render(CampaignState state,HouseId viewer){renderer.begin(title());content(state,viewer);return renderer.finish();}
}
