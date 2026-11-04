package edu.wotfk.campaign;

import edu.wotfk.data.CsvMapAdapter;
import edu.wotfk.domain.HouseId;
import edu.wotfk.houses.HouseFactories;
/** Director provides a reusable recipe; the builder still owns validation. */
public final class CampaignDirector {
    public CampaignState standard(HouseId player, long seed) {
        return new CampaignBuilder().withMap(new CsvMapAdapter()).withFactories(HouseFactories.all())
                .withPlayer(player).withSeed(seed).withRounds(12).build();
    }
}
