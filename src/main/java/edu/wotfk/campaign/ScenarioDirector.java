package edu.wotfk.campaign;

import edu.wotfk.domain.*;
import edu.wotfk.diplomacy.Relation;
import edu.wotfk.intrigue.FeastService;
import java.util.Set;

/** Transparent starting positions for a defence. Actions still use ordinary services. */
public final class ScenarioDirector {
    public static final Set<String> NAMES = Set.of("battle", "feast", "expose", "victim", "council", "military", "political");

    public CampaignState create(String name, long seed) {
        if (!NAMES.contains(name)) throw new IllegalArgumentException("Unknown scenario: " + name);
        CampaignState state = new CampaignDirector().standard(HouseId.STARK, seed);
        // A short-lived armistice separates demonstrations from an ordinary campaign.
        for (HouseId first : HouseId.values()) {
            state.house(first).resources().gain(40, 50, 20);
            for (HouseId second : HouseId.values()) {
                if (first.ordinal() < second.ordinal()) state.diplomacy().between(first, second).truceUntil(20);
            }
        }
        switch (name) {
            case "battle" -> {
                state.house(HouseId.LANNISTER).army().moveTo("LP");
                HouseState player = state.house(HouseId.STARK);
                while (player.army().units().size() < 5) player.army().add(player.newUnit(UnitType.GUARD));
                state.map().territory("TW").reinforce();
                state.map().territory("TW").reinforce();
            }
            case "feast" -> {
                state.map().territory("TW").capture(HouseId.TARGARYEN, 1);
                state.house(HouseId.TARGARYEN).army().moveTo("TW");
                ally(state, HouseId.STARK, HouseId.TARGARYEN, 40);
                // There is no invitation or plot yet. The player must pay to create both.
            }
            case "expose", "victim" -> {
                state.map().territory("TW").capture(HouseId.LANNISTER, 1);
                state.house(HouseId.LANNISTER).army().moveTo("TW");
                state.house(HouseId.LANNISTER).changeReputation(10);
                ally(state, HouseId.STARK, HouseId.LANNISTER, 40);
                // A live invitation, not a pre-resolved outcome. The AI may prepare a plot later.
                new FeastService().invite(state, HouseId.LANNISTER, HouseId.STARK);
            }
            case "council" -> {
                while (state.round() < 12) state.nextRound();
                state.house(HouseId.STARK).resources().gain(0, 0, 60);
                state.house(HouseId.STARK).changeReputation(30);
            }
            case "military" -> {
                for (String id : new String[]{"TW", "KL", "DD", "KW"}) state.map().territory(id).capture(HouseId.STARK, 1);
                while (state.round() < 3) state.nextRound();
            }
            case "political" -> {
                state.map().territory("KL").capture(HouseId.STARK, 1);
                ally(state, HouseId.STARK, HouseId.TARGARYEN, 70);
                ally(state, HouseId.STARK, HouseId.BARATHEON, 70);
                while (state.round() < 3) state.nextRound();
                // Endorsements are not granted here. Ask the two allies in the console.
            }
            default -> throw new IllegalArgumentException("Unknown scenario.");
        }
        state.log("DEMONSTRATION START: " + name + ". Altered starting resources, positions and armistices; normal action rules apply.");
        return state;
    }

    private static void ally(CampaignState state, HouseId a, HouseId b, int trust) {
        Relation relation = state.diplomacy().between(a, b);
        relation.status(Relation.Status.ALLIED);
        relation.changeTrust(trust - relation.trust());
    }
}
