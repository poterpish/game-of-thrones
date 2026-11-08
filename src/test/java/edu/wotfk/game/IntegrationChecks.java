package edu.wotfk.game;

import edu.wotfk.campaign.*;
import edu.wotfk.domain.*;
import edu.wotfk.ui.*;
import java.io.*;
import java.util.List;
import java.util.function.Supplier;
import static edu.wotfk.testsupport.TestSupport.*;

/** Cross-subsystem regressions: errors, pure queries, finish boundaries and UI parsing. */
public final class IntegrationChecks {
    private static String fingerprint(CampaignState state) {
        StringBuilder out = new StringBuilder().append(state.round()).append(state.finished()).append(state.journal());
        for (HouseState house : state.houses()) {
            out.append(house.id()).append(house.resources().summary()).append(house.reputation()).append(house.actions());
            out.append(house.army().location()).append(house.army().morale());
            for (Unit unit : house.army().effectiveUnits(state.round())) out.append(unit.id()).append(unit.name()).append(unit.attack()).append(unit.defense());
        }
        for (Territory territory : state.map().territories()) out.append(territory.id()).append(territory.owner()).append(territory.garrison()).append(territory.ownerSinceRound());
        for (HouseId a : HouseId.values()) for (HouseId b : HouseId.values()) if (a.ordinal() < b.ordinal()) {
            var relation = state.diplomacy().between(a,b);
            out.append(relation.status()).append(relation.trust()).append(relation.truceUntil());
        }
        return out.toString();
    }

    public static void main(String[] args) throws Exception {
        CampaignState state = new CampaignDirector().standard(HouseId.STARK, 91);
        CampaignState same = new CampaignDirector().standard(HouseId.STARK, 91);
        GameFacade game = new GameFacade(state);
        String before = fingerprint(state);
        List<Supplier<ActionResult>> invalid = List.of(
                () -> game.move("ZZ"), () -> game.move("DS"), () -> game.fortify("CR"),
                () -> game.war(HouseId.STARK), () -> game.besiege("WF"),
                () -> game.attack("DS","cautious"), () -> game.attack("TW","impossible"),
                () -> game.equip(0), () -> game.equip(7), () -> game.choose(99),
                () -> game.ally(HouseId.LANNISTER,-1), () -> game.peace(HouseId.LANNISTER,0),
                () -> game.betray(HouseId.LANNISTER), () -> game.endorse(HouseId.LANNISTER),
                () -> game.requestAid(HouseId.LANNISTER), () -> game.spy(HouseId.STARK),
                () -> game.feast(HouseId.STARK), () -> game.accept(99), () -> game.cancelFeast(99),
                () -> game.plot(99), () -> game.expose(99), () -> game.export("../../outside"));
        int index = 0;
        for (Supplier<ActionResult> command : invalid) {
            ActionResult result = command.get();
            check(!result.accepted(),"Invalid command #" + (++index) + " returns an explanation");
            check(before.equals(fingerprint(state)),"Invalid command #" + index + " leaves state unchanged");
        }
        game.status(); game.map(); game.relations(); game.budget(); game.army(); game.journal();
        game.inspect("CR"); game.intel(HouseId.LANNISTER); game.event(); game.obligations(); game.feasts();
        for (String type : new String[]{"economy","military"}) for (String format : new String[]{"console","markdown"}) game.report(type,format);
        check(before.equals(fingerprint(state)),"Every read-only view preserves the world");
        equal(same.random().nextLong(),state.random().nextLong(),"Rejected commands and free views preserve RNG order");

        CampaignState military = new ScenarioDirector().create("military",0);
        military.map().territory("KL").capture(HouseId.LANNISTER,3);
        military.map().territory("KL").capture(HouseId.STARK,3);
        VictoryService victory = new VictoryService();
        victory.evaluate(military); check(!military.finished(),"Recapture resets the capital holding clock");
        military.nextRound(); victory.evaluate(military); check(!military.finished(),"One elapsed round is not enough");
        military.nextRound(); victory.evaluate(military); check(military.finished(),"Two elapsed rounds satisfy the military clock");
        GameFacade closed = new GameFacade(military);
        String finishedBefore = fingerprint(military);
        check(!closed.recruit(UnitType.MILITIA).accepted(),"Finished campaigns reject recruitment");
        check(!closed.endTurn().accepted(),"Finished campaigns cannot advance another round");
        check(finishedBefore.equals(fingerprint(military)),"Post-ending rejections preserve the final state");

        CampaignState tie = new CampaignDirector().standard(HouseId.STARK,0);
        for (HouseState house : tie.houses()) {
            house.changeReputation(60-house.reputation());
            house.resources().pay(new Cost(0,0,house.resources().influence()));
            house.resources().gain(0,0,6);
        }
        while (tie.round()<12) tie.nextRound();
        victory.evaluate(tie);
        check(tie.finished() && tie.winner()==null && tie.ending().startsWith("DIVIDED KINGDOM"),"Equal council leaders produce an explicit draw");

        StringWriter out = new StringWriter();
        CampaignState inputState = new CampaignDirector().standard(HouseId.STARK,0);
        new CampaignConsole(new ConsoleIO(new StringReader("\n  recruit\tmilitia  \nRECRUIT guard\nmove\nequip nope\nquit\n"),out),inputState).run();
        equal(5,inputState.house(HouseId.STARK).army().units().size(),"Tabs, surrounding spaces and case-insensitive commands work");
        check(out.toString().contains("Nothing has changed") && out.toString().contains("Usage: move"),"Blank and incomplete input gives useful messages");
        check(out.toString().contains("Enter a whole number"),"Invalid numeric input does not crash the session");
    }
}
