package edu.wotfk.ui;

import edu.wotfk.campaign.CampaignState;
import java.io.IOException;
import java.util.Locale;
/** Console routing only. Game rules remain outside the UI. */
public final class CampaignConsole {
    private final ConsoleIO console;
    private final CampaignState state;
    public CampaignConsole(ConsoleIO console, CampaignState state) { this.console = console; this.state = state; }
    public void run() throws IOException {
        console.println(CampaignView.status(state));
        console.println("Commands: status, map, help, quit");
        while (!state.finished()) {
            String line = console.readLine("\n  " + state.player().displayName() + " > ");
            if (line == null) return;
            String[] words = line.strip().toLowerCase(Locale.ROOT).split("\s+");
            try {
                switch (words[0]) {
                    case "status" -> console.println(CampaignView.status(state));
                    case "map" -> console.println(state.map().atlas());
                    case "help" -> console.println("status: inspect your house   map: show connections   quit: return to menu");
                    case "quit" -> { return; }
                    default -> console.println("Unknown command. Type help.");
                }
            } catch (IllegalArgumentException | IllegalStateException invalid) {
                console.println("Not allowed: " + invalid.getMessage());
            }
        }
        console.println(state.ending());
    }
    private static void arguments(String[] words, int count, String usage) {
        if (words.length != count) throw new IllegalArgumentException("Usage: " + usage);
    }
}
