package edu.wotfk.story;

import edu.wotfk.domain.Cost;
import java.util.function.Consumer;
public record StoryChoice(String label, Cost cost, Consumer<StoryContext> effect, String result) {
    public StoryChoice { java.util.Objects.requireNonNull(label); java.util.Objects.requireNonNull(cost); java.util.Objects.requireNonNull(effect); java.util.Objects.requireNonNull(result); }
}
