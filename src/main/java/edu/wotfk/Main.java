package edu.wotfk;

import edu.wotfk.ui.ConsoleApplication;
import edu.wotfk.ui.ConsoleIO;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        boolean colorEnabled = false;
        boolean helpRequested = false;

        for (String argument : args) {
            switch (argument) {
                case "--color" -> colorEnabled = true;
                case "--help" -> helpRequested = true;
                default -> {
                    errorWriter().println("Unsupported option. Use --help to see available options.");
                    System.exit(2);
                    return;
                }
            }
        }

        ConsoleIO console = new ConsoleIO(
                new InputStreamReader(System.in, StandardCharsets.UTF_8),
                new OutputStreamWriter(System.out, StandardCharsets.UTF_8)
        );

        if (helpRequested) {
            console.println("War of the Five Kings - console application");
            console.println("Usage: java -jar target/war-of-the-five-kings.jar [--color] [--help]");
            console.println("  --color  Enable optional ANSI colors in a compatible console.");
            console.println("  --help   Show this message without opening the menu.");
            console.println("Default: English text, ASCII frames, no ANSI color codes.");
            return;
        }

        try {
            new ConsoleApplication(console, colorEnabled).run();
        } catch (IOException exception) {
            errorWriter().println("Unable to read console input. Please restart the application.");
            System.exit(1);
        }
    }

    private static PrintWriter errorWriter() {
        return new PrintWriter(
                new OutputStreamWriter(System.err, StandardCharsets.UTF_8), true);
    }
}
