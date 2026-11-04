package edu.wotfk.data;

import edu.wotfk.domain.*;
import edu.wotfk.testsupport.TestSupport;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import static edu.wotfk.testsupport.TestSupport.*;
public final class MapChecks {
    public static void main(String[] args) {
        WorldMap map = new CsvMapAdapter().load();
        equal(12, map.territories().size(), "Adapter loads twelve territories");
        for (HouseId house : HouseId.values()) equal(2, map.ownedBy(house).size(), house + " starts with two territories");
        equal(2, map.ownedBy(null).size(), "Two neutral castles");
        check(map.route("DS", "KL").orElseThrow().sea(), "Sea route has converted type");
        check(map.atlas().contains("King's Landing"), "Atlas includes the capital");
        rejects(() -> map.territory("ZZ"), "Unknown territory is rejected");
        rejects(() -> load("AA;Castle;X;1;1;2;2;"), "Unknown legacy owner rejected");
        rejects(() -> load("AA;Castle;S;1;1;2;2;BB"), "Dangling route rejected");
        rejects(() -> load("AA;Castle;S;1;1;2;2;BB\nBB;Other;L;1;1;2;2;"), "One-way route rejected");
        Resources resources = new Resources(2, 3, 1);
        rejects(() -> resources.pay(new Cost(3, 0, 0)), "Unaffordable spending rejected");
        equal(2, resources.gold(), "Rejected spending is atomic");
        rejects(() -> new Cost(-1, 0, 0), "Negative prices rejected");
        resources.pay(new Cost(2, 3, 1)); equal(0, resources.gold(), "Exact payment is allowed");
    }
    private static WorldMap load(String data) {
        return new CsvMapAdapter(new LegacyMapReader(), () -> new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8))).load();
    }
}
