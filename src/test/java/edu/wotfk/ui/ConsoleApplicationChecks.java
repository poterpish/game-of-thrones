package edu.wotfk.ui;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;

/** Dependency-free checks. Run main() in the IDE or use scripts/check.sh or check.ps1. */
public final class ConsoleApplicationChecks {
    private static int passed;

    private ConsoleApplicationChecks() {
    }

    public static void main(String[] args) throws IOException {
        checkStartupAndExit();
        checkPrologue();
        checkControls();
        checkCredits();
        checkInvalidCommands();
        checkEmptyInputLine();
        checkWhitespaceAroundCommand();
        checkEndOfInput();
        checkRepeatedSections();
        checkPlainOutput();
        checkColoredOutput();
        checkMinimalLayout();
        checkLongTextWrapping();
        checkInputFailure();
        checkOriginalConstructor();
        System.out.println("PASS: " + passed + " console checks completed.");
    }

    private static void checkStartupAndExit() throws IOException {
        String output = run("0\n", false);
        require(output.contains("W A R   O F   T H E   F I V E   K I N G S"), "Missing title");
        require(output.contains("MAIN MENU"), "Missing main menu");
        require(output.contains("UNTIL WE MEET AGAIN"), "Missing farewell");
        require(count(output, "MAIN MENU") == 1, "Exit must not start another menu");
        pass("startup and normal exit");
    }

    private static void checkPrologue() throws IOException {
        String output = run("1\n0\n", false);
        require(output.contains("A CROWN WITHOUT AN HEIR"), "Missing prologue");
        for (String house : new String[]{"STARK", "LANNISTER", "TARGARYEN", "BARATHEON", "GREYJOY"}) {
            require(output.contains(house + " -"), "Missing house perspective: " + house);
        }
        require(output.contains("Twelve rounds remain"), "Missing campaign premise");
        pass("English prologue and five house perspectives");
    }

    private static void checkControls() throws IOException {
        String output = run("2\n0\n", false);
        require(output.contains("YOUR FIRST VISIT TO THE COUNCIL"), "Missing controls");
        require(output.contains("CAMPAIGN:"), "Campaign entry must be explained");
        pass("controls and honest build limits");
    }

    private static void checkCredits() throws IOException {
        String output = run("3\n0\n", false);
        require(output.contains("Alua and Gaukhar"), "Missing authors");
        require(output.contains("Software Design Patterns"), "Missing course");
        require(output.contains("Team Alua and Gaukhar"), "Missing team credit");
        pass("credits and English author names");
    }

    private static void checkInvalidCommands() throws IOException {
        String output = run("abc\n99\n-1\n0\n", false);
        require(count(output, "[!] Unknown command.") == 3, "Expected one hint per invalid command");
        require(output.contains("UNTIL WE MEET AGAIN"), "Input errors must not stop the session");
        pass("unknown text and unsupported numbers");
    }

    private static void checkEmptyInputLine() throws IOException {
        String output = run("\n0\n", false);
        require(count(output, "[!] Unknown command.") == 1, "An empty line needs one hint");
        pass("empty line is not EOF");
    }

    private static void checkWhitespaceAroundCommand() throws IOException {
        String output = run(" \t2  \r\n0\r\n", false);
        require(output.contains("YOUR FIRST VISIT TO THE COUNCIL"), "Command whitespace was not removed");
        require(!output.contains("[!] Unknown command."), "Valid padded command was rejected");
        pass("spaces, tabs, and Windows line endings");
    }

    private static void checkEndOfInput() throws IOException {
        String output = run("", false);
        require(count(output, "MAIN MENU") == 1, "EOF must not repeat the menu");
        require(count(output, "SESSION ENDED") == 1, "EOF must be handled exactly once");
        require(!output.contains("[!] Unknown command."), "EOF is not an invalid command");
        pass("end of input closes the session");
    }

    private static void checkRepeatedSections() throws IOException {
        String output = run("1\n2\n3\n1\n0\n", false);
        require(count(output, "MAIN MENU") == 5, "Sections must return to the menu");
        require(count(output, "A CROWN WITHOUT AN HEIR") == 2, "Sections must remain repeatable");
        pass("sections can be opened repeatedly");
    }

    private static void checkPlainOutput() throws IOException {
        String output = run("1\n2\n3\nwrong\n0\n", false);
        require(output.indexOf('\u001B') < 0, "Default output must not contain ANSI escapes");
        for (int character : output.toCharArray()) {
            require(character <= 127, "Default interface must use ASCII characters only");
        }
        pass("default output is ASCII without color escape codes");
    }

    private static void checkColoredOutput() throws IOException {
        String input = "1\n2\n3\nwrong\n0\n";
        String colored = run(input, true);
        require(colored.contains("\u001B["), "Explicit color mode should include ANSI colors");
        String withoutColor = colored.replaceAll("\u001B\\[[0-9;]*m", "");
        require(withoutColor.equals(run(input, false)), "Color must not alter content or spacing");
        pass("color mode preserves the complete plain-text interface");
    }

    private static void checkMinimalLayout() throws IOException {
        String output = run("1\n2\n3\n0\n", false);
        require(!output.contains("+---") && !output.contains("+==="), "No box borders expected");
        require(output.contains("When you play the game of thrones, you win or you die."), "Missing quote");
        pass("minimal layout and the selected quote");
    }

    private static void checkLongTextWrapping() {
        StringWriter output = new StringWriter();
        ConsoleView view = new ConsoleView(new ConsoleIO(new StringReader(""), output), false);
        String longWord = "x".repeat(170);
        view.showPanel("TEXT WRAPPING", "A sentence that repeats. ".repeat(15), longWord);
        String printed = output.toString();
        assertFrames(printed);
        require(printed.chars().filter(character -> character == 'x').count() == 170,
                "Wrapping must not drop characters from a long word");
        pass("long paragraphs preserve their text");
    }

    private static void checkInputFailure() {
        Reader brokenInput = new Reader() {
            @Override
            public int read(char[] buffer, int offset, int length) throws IOException {
                throw new IOException("Simulated input failure");
            }

            @Override
            public void close() {
                // There is no resource to close in this test input.
            }
        };
        try {
            new ConsoleApplication(new ConsoleIO(brokenInput, new StringWriter())).run();
            throw new AssertionError("The entry point must be allowed to handle an input failure");
        } catch (IOException expected) {
            pass("I/O errors propagate to the entry point");
        }
    }

    private static void checkOriginalConstructor() throws IOException {
        StringWriter output = new StringWriter();
        new ConsoleApplication(new ConsoleIO(new StringReader("0\n"), output)).run();
        require(output.toString().equals(run("0\n", false)), "Original constructor must stay compatible");
        pass("original ConsoleApplication constructor stays compatible");
    }

    private static String run(String input, boolean colorEnabled) throws IOException {
        StringWriter output = new StringWriter();
        ConsoleIO console = new ConsoleIO(new StringReader(input), output);
        new ConsoleApplication(console, colorEnabled).run();
        return output.toString();
    }

    private static void assertFrames(String output) {
        for (String line : output.split("\\R")) {
            if (line.startsWith("|") || line.startsWith("+")) {
                require(line.length() == ConsoleView.FRAME_WIDTH, "Broken frame width: " + line);
            }
        }
    }

    private static int count(String text, String needle) {
        int result = 0;
        int position = 0;
        while ((position = text.indexOf(needle, position)) >= 0) {
            result++;
            position += needle.length();
        }
        return result;
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static void pass(String description) {
        passed++;
        System.out.println("[PASS] " + description);
    }
}
