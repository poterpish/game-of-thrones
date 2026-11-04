package edu.wotfk.domain;

/** Abstract military product; decorators will keep this contract unchanged. */
public interface Unit {
    String id();
    String name();
    HouseId house();
    UnitType type();
    int attack();
    int defense();
    int goldUpkeep();
    int foodUpkeep();
    int coastalBonus();
}
