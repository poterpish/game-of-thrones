package edu.wotfk.intelligence;

import edu.wotfk.domain.HouseId;
import java.util.*;
public final class KnowledgeState {
    private final EnumMap<HouseId,Set<Integer>> evidence=new EnumMap<>(HouseId.class);
    public void recordEvidence(HouseId observer,int feast){evidence.computeIfAbsent(observer,h->new LinkedHashSet<>()).add(feast);}
    public boolean hasEvidence(HouseId observer,int feast){return evidence.getOrDefault(observer,Set.of()).contains(feast);}
    public Set<Integer> evidence(HouseId observer){return Set.copyOf(evidence.getOrDefault(observer,Set.of()));}
    private final EnumMap<HouseId,EnumMap<HouseId,IntelReport>> reports = new EnumMap<>(HouseId.class);
    public KnowledgeState() { for(HouseId house:HouseId.values()) reports.put(house,new EnumMap<>(HouseId.class)); }
    public IntelReport report(HouseId observer,HouseId target) { return reports.get(observer).get(target); }
    public void remember(HouseId observer,IntelReport report) { reports.get(observer).put(report.target(),report); }
}
