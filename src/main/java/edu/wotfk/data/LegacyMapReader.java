package edu.wotfk.data;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
/** Adaptee: a deliberately incompatible semicolon-row API with one-letter owner codes. */
public final class LegacyMapReader {
    public List<String[]> readRows(InputStream input) {
        if (input == null) throw new IllegalArgumentException("Map resource was not found.");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))) {
            List<String[]> rows = new ArrayList<>(); String line; int number = 0;
            while ((line = reader.readLine()) != null) {
                number++; line = line.strip(); if (line.isEmpty() || line.startsWith("#")) continue;
                String[] columns = line.split(";", -1);
                if (columns.length != 8) throw new IllegalArgumentException("Map line " + number + " must have 8 fields.");
                for (int i = 0; i < columns.length; i++) columns[i] = columns[i].strip();
                rows.add(columns);
            }
            return rows;
        } catch (IOException error) { throw new IllegalArgumentException("Cannot read map: " + error.getMessage(), error); }
    }
}
