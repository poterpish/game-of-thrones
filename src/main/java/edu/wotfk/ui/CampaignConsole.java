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
            case "army" -> { arguments(words, 1, "army"); yield game.army(); }
            case "equip" -> { arguments(words, 2, "equip UNIT_NUMBER"); yield game.equip(number(words[1])).toString(); }
            case "command" -> { arguments(words, 1, "command"); yield game.command().toString(); }
            case "event" -> { arguments(words, 1, "event"); yield game.event(); }
            case "choose" -> { arguments(words, 2, "choose NUMBER"); yield game.choose(number(words[1])).toString(); }
            case "ally" -> { arguments(words, 3, "ally HOUSE GIFT_GOLD"); yield game.ally(HouseId.parse(words[1]), number(words[2])).toString(); }
            case "peace" -> { arguments(words, 3, "peace HOUSE GIFT_GOLD"); yield game.peace(HouseId.parse(words[1]), number(words[2])).toString(); }
            case "break" -> { arguments(words, 2, "break HOUSE"); yield game.breakAlliance(HouseId.parse(words[1])).toString(); }
            case "betray" -> { arguments(words, 2, "betray HOUSE"); yield game.betray(HouseId.parse(words[1])).toString(); }
            case "endorse" -> { arguments(words, 2, "endorse HOUSE"); yield game.endorse(HouseId.parse(words[1])).toString(); }
            case "request" -> { arguments(words, 2, "request HOUSE"); yield game.requestAid(HouseId.parse(words[1])).toString(); }
            case "aid" -> { arguments(words, 2, "aid HOUSE"); yield game.aid(HouseId.parse(words[1])).toString(); }
            case "inspect" -> { arguments(words, 2, "inspect ID"); yield game.inspect(words[1]); }
            case "intel" -> { arguments(words, 2, "intel HOUSE"); yield game.intel(HouseId.parse(words[1])); }
            case "spy" -> { arguments(words, 2, "spy HOUSE"); yield game.spy(HouseId.parse(words[1])).toString(); }
            case "guard" -> { arguments(words, 1, "guard"); yield game.guard().toString(); }
            case "feasts" -> { arguments(words, 1, "feasts"); yield game.feasts(); }
            case "feast" -> { arguments(words, 2, "feast HOUSE"); yield game.feast(HouseId.parse(words[1])).toString(); }
            case "accept" -> { arguments(words, 2, "accept FEAST_ID"); yield game.accept(number(words[1])).toString(); }
            case "decline" -> { arguments(words, 2, "decline FEAST_ID"); yield game.decline(number(words[1])).toString(); }
            case "cancel" -> { arguments(words, 2, "cancel FEAST_ID"); yield game.cancelFeast(number(words[1])).toString(); }
            case "obligations" -> { arguments(words, 1, "obligations"); yield game.obligations(); }
            case "loan" -> { arguments(words, 1, "loan"); yield game.loan().toString(); }
            case "expose" -> { arguments(words, 2, "expose FEAST_ID"); yield game.expose(number(words[1])).toString(); }
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
                + "COUNCIL: 2 per territory +3 for KL + influence/3 (floor) + reputation/20 (floor) + endorsing allies. Ties divide the realm.\n"
                + "army  Inspect troops and effects   equip N  1 AP, 3 gold; +2/+2 and +1 upkeep\n"
                + "command  1 AP, 2 influence; support your army for current and next round\n"
                + "event / choose N  Resolve a story decision (no AP; stated resource cost)\n"
                + "ally/peace HOUSE GIFT  1 AP, 2 influence, gift 0..20 gold; gifts are delivered even on refusal\n"
                + "break HOUSE  1 AP; ends alliance with penalties and a truce\n"
                + "betray HOUSE  1 AP, 2 influence; war, -30 reputation, -12 trust with others\n"
                + "endorse HOUSE  Ask an ally to support you: 1 AP, 5 gold, 3 influence\n"
                + "request HOUSE  1 AP; wartime aid request     aid HOUSE  1 AP, 4 gold, 2 food\n"
                + "POLITICAL WIN: hold KL two rounds, influence 10, reputation 50, two endorsing allies.\n"
                + "inspect ID / intel HOUSE  Read public information or dated reports (free)\n"
                + "spy HOUSE  1 AP, 2 gold, 1 influence; risky intelligence mission\n"
                + "guard  1 AP, 2 gold, 1 influence; counterintelligence for current and next round\n"
                + "feasts  Review invitations   feast HOUSE  1 AP, 2 gold/2 food/2 influence; trusted ally/truce required\n"
                + "accept ID  1 food, no AP; travel separately   decline ID  free   cancel ID  host cancels for 1 AP\n"
                + "A peaceful feast gives both houses +2 influence/+8 morale/+3 reputation and +20 mutual trust.\n"
                + "loan  1 AP: borrow 12 gold, repay 15 after three rounds; one active debt\n"
                + "obligations  Review debts, consequences and allied requests\n"
                + "expose ID  1 AP, 1 influence; evidence from scouting is mandatory\n"
                + "end  Complete this round     quit  Return to menu (no save)";
    }
    private static int number(String value) {
        try { return Integer.parseInt(value); }
        catch (NumberFormatException invalid) { throw new IllegalArgumentException("Enter a whole number within the displayed range."); }
    }
    private static void arguments(String[] words, int count, String usage) {
        if (words.length != count) throw new IllegalArgumentException("Usage: " + usage);
    }
}
