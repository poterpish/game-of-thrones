package edu.wotfk.ai;

import edu.wotfk.campaign.CampaignState;
import edu.wotfk.domain.*;
import edu.wotfk.game.ActionService;
import edu.wotfk.combat.*;
import edu.wotfk.diplomacy.*;
import java.util.*;
/** Bounded, readable priorities. It estimates enemy strength instead of reading hidden armies. */
public final class AiController {
    private final ActionService actions = new ActionService();
    private final DiplomacyService diplomacy = new DiplomacyService();
    public void play(CampaignState state, HouseId actor) {
        if (!state.alive(actor) || state.finished()) return;
        HouseState house = state.house(actor);
        if (honorFeast(state, actor)) return;
        Set<String> marchedFrom = new HashSet<>();
        for (int attempt = 0; attempt < 8 && house.actions() > 0; attempt++) {
            int before = house.actions(); Army army = house.army();
            try {
                if (house.resources().food() < 5 && house.resources().gold() >= 3) { actions.supply(state, actor); continue; }
                if (army.morale() < 45 && house.resources().food() >= 2) { actions.rally(state, actor); continue; }
                if (army.units().size() < 4 && state.map().territory(army.location()).owner() == actor
                        && !SiegeService.blockaded(state, army.location()) && house.resources().canPay(house.factory().recruitmentCost(UnitType.MILITIA))) {
                    actions.recruit(state, actor, UnitType.MILITIA); continue;
                }
                if (tryAttack(state, actor)) continue;
                String destination = bestFriendlyStep(state, actor, marchedFrom);
                if (destination != null) {
                    String origin = army.location(); actions.move(state, actor, destination); marchedFrom.add(origin); continue;
                }
                if (house.resources().food() < 12 && house.resources().gold() >= 3) { actions.supply(state, actor); continue; }
                Territory holding = state.map().ownedBy(actor).stream().filter(t -> t.garrison() < 4 && !SiegeService.blockaded(state, t.id())).findFirst().orElse(null);
                if (holding != null && house.resources().canPay(new Cost(3,1,0))) { actions.fortify(state, actor, holding.id()); continue; }
            } catch (IllegalArgumentException | IllegalStateException rejected) {
                // Unaffordable or obsolete opportunities are not forced through the rules.
            }
            if (house.actions() == before) break;
        }
    }
    private boolean honorFeast(CampaignState state, HouseId actor) {
        edu.wotfk.intrigue.Feast meeting = state.feasts().stream().filter(f -> f.active() && (f.host()==actor || (f.guest()==actor && f.status()==edu.wotfk.intrigue.Feast.Status.ACCEPTED))).findFirst().orElse(null);
        if (meeting == null) return false;
        if (meeting.host()==actor) return true; // A host keeps its army at the promised castle.
        HouseState house=state.house(actor);
        while (house.actions()>0 && !house.army().location().equals(meeting.castle())) {
            try {
                if (house.resources().food()<2 && house.resources().gold()>=3) { actions.supply(state,actor); continue; }
                String next = nextMeetingStep(state,actor,meeting.castle());
                if(next==null)break;
                actions.move(state,actor,next);
            } catch(IllegalStateException unavailable) { break; }
        }
        return true;
    }
    private String nextMeetingStep(CampaignState state,HouseId actor,String goal) {
        String origin=state.house(actor).army().location();
        Map<String,String> previous=new LinkedHashMap<>();ArrayDeque<String> queue=new ArrayDeque<>();previous.put(origin,null);queue.add(origin);
        while(!queue.isEmpty()) {
            String id=queue.remove(); if(id.equals(goal))break;
            for(WorldMap.Route route:state.map().routes(id)) {
                String next=route.destination();HouseId owner=state.map().territory(next).owner();
                if(!previous.containsKey(next)&&(owner==actor||state.diplomacy().allied(actor,owner)||new edu.wotfk.intrigue.FeastService().canVisit(state,actor,next))) {
                    previous.put(next,id);queue.add(next);
                }
            }
        }
        if(!previous.containsKey(goal))return null;
        String next=goal;while(previous.get(next)!=null&&!previous.get(next).equals(origin))next=previous.get(next);
        return next.equals(origin)?null:next;
    }
    private boolean tryAttack(CampaignState state, HouseId actor) {
        HouseState house = state.house(actor); Army army = house.army();
        if (army.units().isEmpty() || house.actions() < 2 || house.resources().food() < 2) return false;
        List<Territory> candidates = state.map().routes(army.location()).stream().map(r -> state.map().territory(r.destination()))
                .filter(t -> t.owner() != actor && !state.diplomacy().allied(actor, t.owner()))
                .sorted(Comparator.comparingInt((Territory t) -> t.id().equals("KL") ? 0 : t.owner() == null ? 1 : 2)).toList();
        for (Territory target : candidates) {
            int estimate = estimateDefense(state, actor, target);
            int ownPower = army.attackStrength(state.round(), state.map().coastal(target.id())) * (50 + army.morale()) / 100;
            if (ownPower * 125 / 100 <= estimate) continue;
            if (target.owner() != null && !state.diplomacy().atWar(actor, target.owner())) {
                if (house.actions() < 3 || house.resources().influence() < 2) continue;
                Relation relation = state.diplomacy().between(actor, target.owner());
                if (state.round() <= relation.truceUntil()) continue;
                diplomacy.declareWar(state, actor, target.owner());
            }
            new CombatService().attack(state, actor, target.id(), ownPower > estimate + 5 ? new CautiousStrategy() : new AssaultStrategy());
            return true;
        }
        return false;
    }
    private int estimateDefense(CampaignState state, HouseId actor, Territory target) {
        int estimate = target.fort() * 4 + 6 + (target.owner() == null ? 0 : 12);
        if (target.owner() != null) {
            edu.wotfk.intelligence.IntelReport report = state.knowledge().report(actor, target.owner());
            if (report != null && state.round() - report.observedRound() <= 2) {
                estimate = target.fort() * 4 + report.garrisons().getOrDefault(target.id(), 2) * 3
                        + (report.armyLocation().equals(target.id()) ? report.troops() * 6 : 0);
            }
        }
        return estimate;
    }
    private String bestFriendlyStep(CampaignState state, HouseId actor, Set<String> visited) {
        Army army = state.house(actor).army();
        if (army.units().isEmpty()) return null;
        Map<String,Integer> distance = distances(state, "KL");
        int current = distance.get(army.location());
        return state.map().routes(army.location()).stream().map(WorldMap.Route::destination)
                .filter(id -> !visited.contains(id) && distance.get(id) < current)
                .filter(id -> state.map().territory(id).owner() == actor || state.diplomacy().allied(actor, state.map().territory(id).owner()))
                .min(Comparator.comparingInt(distance::get)).orElse(null);
    }
    private Map<String,Integer> distances(CampaignState state, String goal) {
        Map<String,Integer> result = new LinkedHashMap<>(); ArrayDeque<String> queue = new ArrayDeque<>();
        result.put(goal, 0); queue.add(goal);
        while (!queue.isEmpty()) {
            String id = queue.remove();
            for (WorldMap.Route route : state.map().routes(id)) if (!result.containsKey(route.destination())) {
                result.put(route.destination(), result.get(id) + 1); queue.add(route.destination());
            }
        }
        return result;
    }
}
