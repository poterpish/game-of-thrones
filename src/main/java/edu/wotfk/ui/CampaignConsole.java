package edu.wotfk.ui;

import edu.wotfk.campaign.CampaignState;
import edu.wotfk.domain.*;
import edu.wotfk.game.GameFacade;
import java.io.IOException;
import java.util.Locale;
/** Parses text only. Every state-changing command goes through the facade. */
public final class CampaignConsole {
    private final ConsoleIO console;
    private final GameFacade game;
    public CampaignConsole(ConsoleIO console, CampaignState state) { this.console = console; this.game = new GameFacade(state); }
    public void run() throws IOException {
        console.println(game.status()); console.println("Type help for commands and costs. Views are free.");
        while (!game.finished()) {
            String line = console.readLine("\n  " + game.playerName() + " > ");
            if (line == null) return;
            String[] words = line.strip().toLowerCase(Locale.ROOT).split("\s+");
            if (words.length == 1 && words[0].equals("quit")) return;
            try {
                console.println(dispatch(words));
                if (words[0].equals("end") && !game.finished()) console.println(game.status());
            } catch (IllegalArgumentException | IllegalStateException invalid) {
                console.println("Not allowed: " + invalid.getMessage());
            }
        }
        console.println(game.ending());
    }
    private String dispatch(String[] words) {
        return switch (words[0]) {
            case "status" -> { arguments(words, 1, "status"); yield game.status(); }
            case "map" -> { arguments(words, 1, "map"); yield game.map(); }
            case "relations" -> { arguments(words, 1, "relations"); yield game.relations(); }
            case "budget" -> { arguments(words, 1, "budget"); yield game.budget(); }
            case "recruit" -> { arguments(words, 2, "recruit militia|guard"); yield game.recruit(UnitType.parse(words[1])).toString(); }
            case "move" -> { arguments(words, 2, "move TERRITORY_ID"); yield game.move(words[1]).toString(); }
            case "fortify" -> { arguments(words, 2, "fortify TERRITORY_ID"); yield game.fortify(words[1]).toString(); }
            case "supply" -> { arguments(words, 1, "supply"); yield game.supply().toString(); }
            case "rally" -> { arguments(words, 1, "rally"); yield game.rally().toString(); }
            case "war" -> { arguments(words, 2, "war HOUSE"); yield game.war(HouseId.parse(words[1])).toString(); }
            case "skirmish" -> { arguments(words, 3, "skirmish ID assault|cautious"); yield game.skirmish(words[1], words[2]).toString(); }
            case "journal" -> { arguments(words, 1, "journal"); yield game.journal(); }
            case "besiege" -> { arguments(words, 2, "besiege ID"); yield game.besiege(words[1]).toString(); }
            case "lift" -> { arguments(words, 1, "lift"); yield game.liftSiege().toString(); }
            case "attack" -> { arguments(words, 3, "attack ID assault|cautious"); yield game.attack(words[1], words[2]).toString(); }
            case "end" -> { arguments(words, 1, "end"); yield game.endTurn().toString(); }
            case "help" -> help();
            default -> "Unknown command. Type help.";
        };
    }
    private String help() {
        return "VIEWS: status, map, relations, budget (free)\n"
                + "recruit militia|guard  1 AP; militia 3 gold/2 food; guard 6 gold/2 food/1 influence (Lannister guard 7 gold)\n"
                + "move ID  1 AP and 1 food; sea 2 food except Greyjoy\n"
                + "fortify ID  1 AP, 3 gold, 1 food; add one garrison (max 8)\n"
                + "supply  1 AP, 3 gold -> 6 food     rally  1 AP, 2 food -> +15 morale\n"
                + "war HOUSE  1 AP, 2 influence; no treaty violations\n"
                + "skirmish ID assault|cautious  2 AP, 2 food; weakens a castle, does not capture it\n"
                + "besiege ID  1 AP, 2 food; wait to weaken forts     lift  Cancel your siege (free)\n"
                + "attack ID assault|cautious  2 AP, 2 food; victory captures the castle\n"
                + "journal  Read public world events\n"
                + "WIN: hold KL for two full rounds and own six territories. Otherwise the council scores round 12.\n"
                + "COUNCIL: 2 per territory +3 for KL + influence/3 (floor) + reputation/20 (floor). Ties divide the realm.\n"
                + "end  Complete this round     quit  Return to menu (no save)";
    }
    private static void arguments(String[] words, int count, String usage) {
        if (words.length != count) throw new IllegalArgumentException("Usage: " + usage);
    }
}
