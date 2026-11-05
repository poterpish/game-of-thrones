package edu.wotfk.combat;

import edu.wotfk.campaign.*;
import edu.wotfk.domain.*;
import edu.wotfk.game.*;
import edu.wotfk.events.*;
import edu.wotfk.economy.*;
import static edu.wotfk.testsupport.TestSupport.*;
public final class CaptureChecks {
    public static void main(String[] args) {
        CampaignState s = new CampaignDirector().standard(HouseId.STARK, 7); GameFacade game = new GameFacade(s);
        int[] notified = {0};
        s.events().subscribe(e -> { if (e instanceof TerritoryCaptured captured) { equal(HouseId.STARK, s.map().territory(captured.territory()).owner(), "Observer sees committed ownership"); notified[0]++; } });
        check(game.attack("TW", "cautious").accepted(), "Legal castle attack accepted");
        equal(HouseId.STARK, s.map().territory("TW").owner(), "Victory changes owner");
        equal("TW", s.house(HouseId.STARK).army().location(), "Winning army enters castle");
        equal(1, notified[0], "Capture published once");
        check(game.journal().contains("captured TW"), "Chronicle observer records capture");
        check(s.triggers().happened("capture", HouseId.STARK), "Trigger observer remembers capture milestone");
        check(!s.notifications().drain().isEmpty(), "Notification observer queues messages");
        check(s.notifications().drain().isEmpty(), "Notifications are consumed once");
        equal(8, new EconomyService().forecast(s, HouseId.STARK).goldIncome(), "Captured castle now contributes income");
        CampaignState siege = new CampaignDirector().standard(HouseId.STARK, 1); GameFacade g = new GameFacade(siege);
        check(g.besiege("TW").accepted(), "Siege preparation allowed");
        equal(null, siege.map().territory("TW").owner(), "Siege alone does not capture");
        new SiegeService().advance(siege); equal(1, siege.sieges().get(HouseId.STARK).progress(), "Siege progresses each settlement");
        check(!g.besiege("TW").accepted(), "Duplicate siege rejected");
        check(g.move("WH").accepted(), "Movement lifts siege");
        check(siege.sieges().isEmpty(), "Moving removes old blockade");
        WorldEventBus bus = new WorldEventBus(); int[] listeners = {0};
        bus.subscribe(e -> { throw new IllegalStateException("Broken optional observer"); });
        bus.subscribe(e -> listeners[0]++); bus.publish(new WorldNotice("test", 1, HouseId.STARK, "test"));
        equal(1, listeners[0], "One broken observer does not stop others");
        equal(1, bus.failures().size(), "Observer failure is recorded, not hidden");
    }
}
