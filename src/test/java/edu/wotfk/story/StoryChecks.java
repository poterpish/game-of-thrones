package edu.wotfk.story;

import edu.wotfk.campaign.*;
import edu.wotfk.domain.*;
import static edu.wotfk.testsupport.TestSupport.*;
public final class StoryChecks {
    public static void main(String[] args) {
        CampaignState s = new CampaignDirector().standard(HouseId.STARK,1);
        WinterEventCreator creator = new WinterEventCreator();
        check(!creator.offer(s,HouseId.STARK), "Winter event cannot appear before its trigger");
        for(int i=0;i<4;i++) s.nextRound();
        check(creator.offer(s,HouseId.STARK), "Factory Method offers an eligible event");
        check(s.pendingEvents().get(HouseId.STARK) instanceof WinterEvent, "Concrete creator returns its concrete product");
        check(!creator.offer(s,HouseId.STARK), "Creator cannot offer the same event twice");
        int gold=s.house(HouseId.STARK).resources().gold(); int food=s.house(HouseId.STARK).resources().food();
        new EventService().choose(s,HouseId.STARK,1);
        equal(gold-5,s.house(HouseId.STARK).resources().gold(), "Event choice spends its price");
        equal(food+10,s.house(HouseId.STARK).resources().food(), "Event consequence changes resources");
        equal(3,s.house(HouseId.STARK).actions(), "Story decisions do not consume AP");
        rejects(() -> new EventService().choose(s,HouseId.STARK,1), "Resolved choice cannot be applied twice");
        check(!creator.offer(s,HouseId.STARK), "Resolved event is not offered again");
        check(s.storyFlags().contains("winter-ready:STARK"), "Story decision records a narrative consequence");
        for(int i=0;i<5;i++) s.nextRound();
        check(new CouncilEventCreator().offer(s,HouseId.STARK), "Another creator uses the same offer algorithm");
        check(s.pendingEvents().get(HouseId.STARK) instanceof CouncilWarningEvent, "Second creator returns a different event product");
    }
}
