package edu.wotfk.economy;

import edu.wotfk.domain.Army;
import edu.wotfk.domain.Territory;
/** Both overloads participate in double dispatch through each element's accept method. */
public interface WorldVisitor {
    void visit(Territory territory);
    void visit(Army army);
}
