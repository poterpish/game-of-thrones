package edu.wotfk.game;

import edu.wotfk.campaign.*;
import edu.wotfk.domain.*;
import edu.wotfk.diplomacy.*;
import edu.wotfk.economy.*;
import edu.wotfk.ui.CampaignView;
import java.util.function.Supplier;
/** Facade translates UI operations into subsystem calls; no battle or treaty formula belongs here. */
public final class GameFacade {
    private final CampaignState state;
    private final ActionService actions = new ActionService();
    private final DiplomacyService diplomacy = new DiplomacyService();
    private final TurnService turns = new TurnService();
    public GameFacade(CampaignState state) { this.state = java.util.Objects.requireNonNull(state); }
    private ActionResult execute(Supplier<String> operation) {
        try { return new ActionResult(true, operation.get()); }
        catch (IllegalArgumentException | IllegalStateException rejected) { return new ActionResult(false, rejected.getMessage()); }
    }
    public String status() { return CampaignView.status(state); }
    public String map() { return state.map().atlas(); }
    public String relations() { return diplomacy.overview(state, state.player()); }
    public String budget() { return new EconomyService().forecast(state, state.player()).summary(); }
    public String playerName() { return state.player().displayName(); }
    public boolean finished() { return state.finished(); }
    public String ending() { return state.ending(); }
    public ActionResult recruit(UnitType type) { return execute(() -> actions.recruit(state, state.player(), type)); }
    public ActionResult move(String destination) { return execute(() -> actions.move(state, state.player(), destination)); }
    public ActionResult fortify(String id) { return execute(() -> actions.fortify(state, state.player(), id)); }
    public ActionResult supply() { return execute(() -> actions.supply(state, state.player())); }
    public ActionResult rally() { return execute(() -> actions.rally(state, state.player())); }
    public ActionResult war(HouseId target) { return execute(() -> diplomacy.declareWar(state, state.player(), target)); }
    public ActionResult skirmish(String target, String tactic) {
        return execute(() -> new edu.wotfk.combat.BattleService().skirmish(state, state.player(), target, edu.wotfk.combat.BattleStrategies.named(tactic)));
    }
    public String journal() { return String.join("\n", state.journal()); }
    public ActionResult besiege(String id) { return execute(() -> new edu.wotfk.combat.SiegeService().begin(state, state.player(), id)); }
    public ActionResult liftSiege() { return execute(() -> new edu.wotfk.combat.SiegeService().lift(state, state.player())); }
    public ActionResult attack(String id, String tactic) {
        return execute(() -> new edu.wotfk.combat.CombatService().attack(state, state.player(), id, edu.wotfk.combat.BattleStrategies.named(tactic)));
    }
    public String army() { return new edu.wotfk.units.EquipmentService().overview(state, state.player()); }
    public ActionResult equip(int unit) { return execute(() -> new edu.wotfk.units.EquipmentService().equip(state, state.player(), unit)); }
    public ActionResult command() { return execute(() -> new edu.wotfk.units.EquipmentService().command(state, state.player())); }
    public String event() { return new edu.wotfk.story.EventService().describe(state, state.player()); }
    public ActionResult choose(int number) { return execute(() -> new edu.wotfk.story.EventService().choose(state, state.player(), number)); }
    public ActionResult endTurn() { return execute(() -> turns.endRound(state)); }
}
