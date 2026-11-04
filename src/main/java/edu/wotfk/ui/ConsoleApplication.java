package edu.wotfk.ui;

import java.io.IOException;
import java.util.Objects;

/** Coordinates the menu. Future game rules belong in separate game services. */
public final class ConsoleApplication {
    private final ConsoleIO console;
    private final ConsoleView view;

    /** Keeps the original entry point available; plain output is the safe default. */
    public ConsoleApplication(ConsoleIO console) {
        this(console, false);
    }

    public ConsoleApplication(ConsoleIO console, boolean colorEnabled) {
        this.console = Objects.requireNonNull(console, "console");
        this.view = new ConsoleView(console, colorEnabled);
    }

    public void run() throws IOException {
        view.showBanner();

        while (true) {
            view.showMenu();
            String command = console.readLine(view.inputPrompt());
            console.println();

            // EOF ends the session rather than creating an endless error loop.
            if (command == null) {
                view.showPanel("SESSION ENDED",
                        "Input has been closed. The council will gather another day.",
                        "Farewell, and may your house endure.");
                return;
            }

            // Comparing strings makes unexpected text safe: no integer parsing is needed.
            switch (command.strip()) {
                case "1" -> showStory();
                case "2" -> showHelp();
                case "3" -> showCredits();
                case "0" -> {
                    view.showPanel("UNTIL WE MEET AGAIN",
                            "The banners fall silent. The Iron Throne awaits.",
                            "Thank you for visiting War of the Five Kings.");
                    return;
                }
                default -> view.showInvalidCommand();
            }
        }
    }

    private void showStory() {
        view.showPanel("THE REALM  /  A CROWN WITHOUT AN HEIR",
                "The king is dead, and no heir commands the loyalty of the realm.",
                "A council of regents holds King's Landing, but its authority is fading. "
                        + "Debts go unpaid, roads grow dangerous, and the great houses raise their banners.",
                "",
                "STARK - The North seeks safety, loyalty, and guarantees that its oaths will be honored.",
                "LANNISTER - The wealth of the West may buy an army, but it cannot buy lasting trust.",
                "TARGARYEN - The exiles have returned early. Their claim needs allies as much as soldiers.",
                "BARATHEON - A united cause demands a lawful crown, even when compromise would be easier.",
                "GREYJOY - The islands reach for the coast, its sea routes, and recognition of their power.",
                "",
                "Twelve rounds remain before the Great Council. Winter is approaching. "
                        + "Every promise, siege, and betrayal will shape the kingdom that survives.",
                "",
                "This is an alternate-history setting for an educational project. "
                        + "Campaign mechanics are planned, not playable in this first package.");
    }

    private void showHelp() {
        view.showPanel("CONTROLS  /  YOUR FIRST VISIT TO THE COUNCIL",
                "Type a menu number, then press Enter. The main menu returns after each information screen.",
                "",
                "[1] Read the realm's prologue.",
                "[2] Review these controls.",
                "[3] View the project credits.",
                "[0] Exit the application.",
                "",
                "Leading and trailing spaces are ignored. An empty line or an unknown command "
                        + "shows a hint without closing the application.",
                "",
                "CURRENT BUILD: English menu, story, credits, and safe input handling.",
                "NOT YET AVAILABLE: house selection, map, turns, battles, and diplomacy.",
                "",
                "Optional launch flag: --color enables ANSI colors in a compatible console. "
                        + "Without it, all frames and commands remain available in plain text.");
    }

    private void showCredits() {
        view.showPanel("CREDITS  /  THE MAESTERS' LEDGER",
                "Project       War of the Five Kings",
                "Team          Alua and Gaukhar",
                "Course        Software Design Patterns");
    }
}
