package edu.wotfk.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Objects;

/** Reads and writes text without knowing about houses, resources, or game rules. */
public final class ConsoleIO {
    private final BufferedReader input;
    private final PrintWriter output;

    public ConsoleIO(Reader input, Writer output) {
        this.input = new BufferedReader(Objects.requireNonNull(input, "input"));
        this.output = new PrintWriter(Objects.requireNonNull(output, "output"), true);
    }

    /** Returns null when input ends. An empty line is a different, valid result. */
    public String readLine(String prompt) throws IOException {
        output.print(prompt);
        output.flush(); // The player must see the prompt before input is requested.
        return input.readLine();
    }

    public void println(String text) {
        output.println(text);
    }

    public void println() {
        output.println();
    }
}
