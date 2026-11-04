package edu.wotfk.ui;

import java.util.Objects;

/** Minimal console presentation without boxes or ASCII art. */
public final class ConsoleView {
    // Retained for compatibility with the existing console checks.
    public static final int FRAME_WIDTH = 76;

    private static final String RESET = "\u001B[0m";
    private static final String GOLD = "\u001B[1;33m";
    private static final String CYAN = "\u001B[1;36m";
    private static final String MUTED = "\u001B[90m";
    private static final String WARNING = "\u001B[1;31m";

    private final ConsoleIO console;
    private final boolean colorEnabled;

    public ConsoleView(ConsoleIO console, boolean colorEnabled) {
        this.console = Objects.requireNonNull(console, "console");
        this.colorEnabled = colorEnabled;
    }

    public void showBanner() {
        console.println();
        console.println(painted("  W A R   O F   T H E   F I V E   K I N G S", GOLD));
        console.println(painted("  When you play the game of thrones, you win or you die.", MUTED));
        console.println();
        console.println(painted("  STARK  /  LANNISTER  /  TARGARYEN  /  BARATHEON  /  GREYJOY", CYAN));
    }

    public void showMenu() {
        console.println();
        console.println(painted("  MAIN MENU", GOLD));
        console.println();
        console.println("  1   The realm     Discover the story and the five houses");
        console.println("  2   Controls      Learn how to navigate");
        console.println("  3   Credits       Meet the development team");
        console.println("  4   Atlas         Inspect the twelve connected territories");
        console.println("  5   New campaign  Choose your house and raise its banners");
        console.println("  0   Exit          Leave the game");
        console.println();
        console.println(painted("  Twelve rounds. One crown. Your decisions shape the realm.", MUTED));
    }

    public String inputPrompt() {
        return painted("\n  Choose an option (0-5): ", CYAN);
    }

    public void showPanel(String title, String... paragraphs) {
        console.println();
        console.println(painted("  " + Objects.requireNonNull(title, "title"), GOLD));
        console.println();
        for (String paragraph : paragraphs) {
            String text = Objects.requireNonNull(paragraph, "paragraph");
            if (text.isBlank()) {
                console.println();
            } else {
                printWrapped(text, 72);
            }
        }
        console.println();
    }

    public void showInvalidCommand() {
        console.println(painted("  [!] Unknown command. Enter a menu number from 0 to 5, then press Enter.", WARNING));
        console.println("      Nothing has been changed. Please try again.");
    }

    private void printWrapped(String text, int width) {
        StringBuilder line = new StringBuilder("  ");
        for (String word : text.strip().split("\\s+")) {
            if (line.length() > 2 && line.length() + word.length() + 1 > width) {
                console.println(line.toString());
                line = new StringBuilder("  ");
            }
            if (line.length() > 2) line.append(' ');
            line.append(word);
        }
        console.println(line.toString());
    }

    private String painted(String text, String color) {
        return colorEnabled ? color + text + RESET : text;
    }
}
