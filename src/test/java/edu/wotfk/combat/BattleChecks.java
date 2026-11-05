package edu.wotfk.combat;

import edu.wotfk.campaign.*;
import edu.wotfk.domain.*;
import edu.wotfk.game.*;
import java.util.Random;
import static edu.wotfk.testsupport.TestSupport.*;
public final class BattleChecks {
    public static void main(String[] args) {
        BattleResolver resolver = new BattleResolver();
        BattleOutcome a = resolver.resolve(30, 20, 70, 5, 3, new AssaultStrategy(), new Random(7));
        BattleOutcome b = resolver.resolve(30, 20, 70, 5, 3, new AssaultStrategy(), new Random(7));
        equal(a, b, "Equal seed and inputs reproduce a battle");
        BattleOutcome cautious = resolver.resolve(30, 20, 70, 5, 3, new CautiousStrategy(), new Random(7));
        check(a.attackScore() > cautious.attackScore(), "Assault increases pressure");
        check(a.attackerLosses() > cautious.attackerLosses(), "Cautious victory conserves troops");
        CampaignState state = new CampaignDirector().standard(HouseId.STARK, 7);
        GameFacade game = new GameFacade(state);
        check(!game.skirmish("KL", "assault").accepted(), "Non-adjacent combat rejected");
        equal(3, state.house(HouseId.STARK).actions(), "Illegal battle spends no AP");
        check(game.skirmish("TW", "assault").accepted(), "Neutral castle skirmish allowed");
        equal(null, state.map().territory("TW").owner(), "Skirmish does not transfer ownership");
        equal(22, state.house(HouseId.STARK).resources().food(), "Battle costs two food");
        equal(1, state.house(HouseId.STARK).actions(), "Battle costs two AP");
        check(!game.skirmish("TW", "assault").accepted(), "Insufficient action points block second battle");
        rejects(() -> BattleStrategies.named("random"), "Unknown tactic rejected");
    }
}
