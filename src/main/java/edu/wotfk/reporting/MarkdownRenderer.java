package edu.wotfk.reporting;

import java.util.*;
public final class MarkdownRenderer implements ReportRenderer {
    private final StringBuilder out=new StringBuilder();
    @Override public void begin(String title){out.setLength(0);out.append("# ").append(title).append("\n\n");}
    @Override public void paragraph(String text){out.append(text).append("\n\n");}
    @Override public void table(List<String> headings,List<List<String>> rows){
        row(headings);row(Collections.nCopies(headings.size(),"---"));
        for(List<String> row:rows){if(row.size()!=headings.size())throw new IllegalArgumentException("Report column mismatch.");row(row);}out.append("\n");
    }
    private void row(List<String> cells){out.append("| ").append(String.join(" | ",cells.stream().map(s->s.replace("|","\\|").replace("\n"," ")).toList())).append(" |\n");}
    @Override public String finish(){return out.toString().stripTrailing();}
}
