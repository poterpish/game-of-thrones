package edu.wotfk.reporting;

import java.util.List;
/** Bridge implementation axis: presentation, independent of report subject. */
public interface ReportRenderer {
    void begin(String title);
    void paragraph(String text);
    void table(List<String> headings,List<List<String>> rows);
    String finish();
}
