package edu.wotfk.economy;

import edu.wotfk.domain.*;
/** A second operation over the same element hierarchy; no instanceof traversal. */
public final class AssetSummaryVisitor implements WorldVisitor {
    private final HouseId owner;
    private final int round;
    private int territories;
    private int fortLevels;
    private int detachments;
    private int attack;
    private int defense;
    public AssetSummaryVisitor(HouseId owner,int round){this.owner=owner;this.round=round;}
    @Override public void visit(Territory territory){if(territory.owner()==owner){territories++;fortLevels+=territory.fort();}}
    @Override public void visit(Army army){if(army.owner()==owner){detachments+=army.units().size();attack+=army.attackStrength(round,false);defense+=army.defenseStrength(round,false);}}
    public String summary(){return territories+" territories, "+fortLevels+" total fort levels, "+detachments+" detachments; base effective attack "+attack+", defense "+defense+" (before morale, terrain and tactic).";}
    public int territories(){return territories;}public int detachments(){return detachments;}
}
