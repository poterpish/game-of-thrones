package edu.wotfk;

import edu.wotfk.campaign.ScenarioDirector;
import java.util.Locale;

/** Validates startup arguments without touching a campaign or consuming randomness. */
public record LaunchOptions(boolean color, boolean help, String scenario, long seed) {
    public static LaunchOptions parse(String[] args) {
        boolean color = false;
        boolean help = false;
        String scenario = null;
        long seed = 20261104L;
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--color" -> color = true;
                case "--help" -> help = true;
                case "--scenario" -> {
                    if (++i == args.length) throw new IllegalArgumentException("--scenario requires a name.");
                    scenario = args[i].toLowerCase(Locale.ROOT);
                    if (!ScenarioDirector.NAMES.contains(scenario)) throw new IllegalArgumentException("Unknown demonstration scenario: " + scenario);
                }
                case "--seed" -> {
                    if (++i == args.length) throw new IllegalArgumentException("--seed requires a 64-bit whole number.");
                    try { seed = Long.parseLong(args[i]); }
                    catch (NumberFormatException badNumber) { throw new IllegalArgumentException("Seed must be a 64-bit whole number."); }
                }
                default -> throw new IllegalArgumentException("Unsupported option: " + args[i] + ". Use --help.");
            }
        }
        return new LaunchOptions(color, help, scenario, seed);
    }
}
