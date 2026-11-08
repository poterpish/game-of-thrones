package edu.wotfk;

import edu.wotfk.ui.ConsoleApplication;
import edu.wotfk.ui.ConsoleIO;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

public final class Main {
    private Main() { }

    public static void main(String[] args) {
        LaunchOptions options;
        try { options = LaunchOptions.parse(args); }
        catch (IllegalArgumentException invalid) {
            errorWriter().println(invalid.getMessage());
            System.exit(2);
            return;
        }
        ConsoleIO console = new ConsoleIO(
                new InputStreamReader(System.in, StandardCharsets.UTF_8),
                new OutputStreamWriter(System.out, StandardCharsets.UTF_8));
        if (options.help()) {
            console.println("War of the Five Kings - turn-based console strategy");
            console.println("Usage: java -jar target/war-of-the-five-kings.jar [--color] [--seed NUMBER] [--scenario NAME] [--help]");
            console.println("  --color          Enable optional ANSI colors in a compatible console.");
            console.println("  --seed NUMBER    Default campaign seed; the normal campaign menu can override it.");
            console.println("  --scenario NAME  Start a transparent demonstration: battle, feast, expose, victim, council, military, political.");
            console.println("  --help           Show this message without opening the menu.");
            console.println("Default: English text, minimal layout, no ANSI colors. Commands and views do not require colors.");
            return;
        }
        try {
            new ConsoleApplication(console, options.color(), options.scenario(), options.seed()).run();
        } catch (IOException exception) {
            errorWriter().println("Unable to read console input. Please restart the application.");
            System.exit(1);
        }
    }

    private static PrintWriter errorWriter() {
        return new PrintWriter(new OutputStreamWriter(System.err, StandardCharsets.UTF_8), true);
    }
}
