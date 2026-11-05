package edu.wotfk.domain;

import java.util.*;
public final class Army implements edu.wotfk.economy.WorldElement {
    @Override public void accept(edu.wotfk.economy.WorldVisitor visitor) { visitor.visit(this); }
    public static final int CAPACITY = 6;
    private final HouseId owner;
    private String location;
    private int morale;
    private final Set<String> equipped = new HashSet<>();
    private int commanderUntil;
    private int exhaustedUntil;
    public boolean equipped(String id) { return equipped.contains(id); }
    public boolean commanded(int round) { return commanderUntil >= round; }
    public void equip(String id) {
        if (units.stream().noneMatch(u -> u.id().equals(id)) || !equipped.add(id)) throw new IllegalStateException("No such unequipped unit.");
    }
    public void supportUntil(int round) { commanderUntil = Math.max(commanderUntil, round); }
    public void exhaustUntil(int round) { exhaustedUntil = Math.max(exhaustedUntil, round); }
    /** Rebuild only the active chain; expired wrappers never remain attached to permanent units. */
    public List<Unit> effectiveUnits(int round) {
        List<Unit> result = new ArrayList<>();
        for (Unit base : units) {
            Unit current = base;
            if (equipped.contains(base.id())) current = new edu.wotfk.units.EquippedUnit(current);
            if (commanderUntil >= round) current = new edu.wotfk.units.CommanderSupportedUnit(current);
            if (exhaustedUntil >= round) current = new edu.wotfk.units.ExhaustedUnit(current);
            result.add(current);
        }
        return List.copyOf(result);
    }
    private final List<Unit> units = new ArrayList<>();
    public Army(HouseId owner, String location, int morale) {
        this.owner = Objects.requireNonNull(owner); this.location = Objects.requireNonNull(location);
        if (morale < 0 || morale > 100) throw new IllegalArgumentException("Morale out of range.");
        this.morale = morale;
    }
    public HouseId owner() { return owner; }
    public String location() { return location; }
    public int morale() { return morale; }
    public List<Unit> units() { return Collections.unmodifiableList(units); }
    public void moveTo(String id) { location = Objects.requireNonNull(id); }
    public void changeMorale(int delta) { morale = Math.max(0, Math.min(100, morale + delta)); }
    public void add(Unit unit) {
        if (unit.house() != owner || units.size() >= CAPACITY || units.stream().anyMatch(u -> u.id().equals(unit.id())))
            throw new IllegalStateException("Unit has wrong family, duplicate identity or no army slot.");
        units.add(unit);
    }
    /** Removes the newest detachments first, making losses deterministic. */
    public int lose(int number) {
        if (number < 0) throw new IllegalArgumentException("Negative losses.");
        int losses = Math.min(number, units.size());
        for (int i = 0; i < losses; i++) { Unit lost = units.remove(units.size() - 1); equipped.remove(lost.id()); }
        return losses;
    }
    public int attackStrength(int round, boolean coast) { return effectiveUnits(round).stream().mapToInt(u -> u.attack() + (coast ? u.coastalBonus() : 0)).sum(); }
    public int defenseStrength(int round, boolean coast) { return effectiveUnits(round).stream().mapToInt(u -> u.defense() + (coast ? u.coastalBonus() : 0)).sum(); }
}
