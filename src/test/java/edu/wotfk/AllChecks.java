package edu.wotfk;

/** Runs every dependency-free check suite. scripts/check.* is the test entry point. */
public final class AllChecks {
    private AllChecks() { }
    public static void main(String[] args) throws Exception {
        String[] suites = {
                "edu.wotfk.campaign.CampaignChecks",
                "edu.wotfk.data.MapChecks",
                "edu.wotfk.economy.EconomyChecks",
                "edu.wotfk.game.ActionChecks",
                "edu.wotfk.ui.ConsoleApplicationChecks"
        };
        for (String suite : suites) {
            System.out.println("\nSUITE: " + suite);
            try {
                Class.forName(suite).getMethod("main", String[].class)
                    .invoke(null, (Object) new String[0]);
            } catch (java.lang.reflect.InvocationTargetException failure) {
                Throwable cause = failure.getCause();
                if (cause instanceof Exception exception) throw exception;
                if (cause instanceof Error error) throw error;
                throw new RuntimeException(cause);
            }
        }
        System.out.println("\nALL " + suites.length + " SUITES PASSED");
    }
}
