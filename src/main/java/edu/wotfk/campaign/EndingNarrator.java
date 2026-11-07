package edu.wotfk.campaign;

import edu.wotfk.domain.HouseId;
public final class EndingNarrator {
    public String narrate(CampaignState state, HouseId winner, String route) {
        String result = winner == state.player() ? "VICTORY" : "DEFEAT";
        String legacy = switch (winner) {
            case STARK -> "The northern oaths become the foundation of a new compact.";
            case LANNISTER -> "The realm's roads reopen beneath the lion's gold.";
            case TARGARYEN -> "The exiles return, now responsible for the kingdom they claimed.";
            case BARATHEON -> "A restored law binds the surviving houses to the crown.";
            case GREYJOY -> "The sea lords turn their coastal footholds into a royal domain.";
        };
        if(state.storyFlags().contains("red-feast:"+winner))legacy+=" The violation of guest right will be remembered long after the coronation.";
        if(state.storyFlags().contains("amnesty:"+winner))legacy+=" Former enemies keep the protections promised in the amnesty.";
        if(state.storyFlags().contains("safe-ports:"+winner))legacy+=" Merchant convoys, not raids, now sustain the coastal crown.";
        if(state.storyFlags().contains("oaths:"+winner))legacy+=" The lords remember that their oaths were purchased with respect rather than fear.";
        if(state.storyFlags().contains("mercy:"+winner))legacy+=" The pardons provide the new regime with willing suppliers.";
        return result + ": " + winner.displayName() + " wins by " + route + ".\n" + legacy
                + (state.house(winner).reputation() < 40 ? "\nA fearful peace follows: the crown has power, but little trust." : "\nThe surviving houses accept a fragile but credible peace.");
    }
}
