package edu.wotfk.reporting;

import java.util.*;
public final class ConsoleRenderer implements ReportRenderer {
    private final StringBuilder out=new StringBuilder();
    @Override public void begin(String title){out.setLength(0);out.append(title.toUpperCase(Locale.ROOT)).append("\n\n");}
    @Override public void paragraph(String text){out.append(text).append("\n\n");}
    @Override public void table(List<String> headings,List<List<String>> rows){
        int[] widths=headings.stream().mapToInt(String::length).toArray();
        for(List<String> row:rows){if(row.size()!=headings.size())throw new IllegalArgumentException("Report column mismatch.");for(int i=0;i<widths.length;i++)widths[i]=Math.max(widths[i],row.get(i).length());}
        row(headings,widths);for(List<String> row:rows)row(row,widths);out.append("\n");
    }
    private void row(List<String> cells,int[] widths){for(int i=0;i<cells.size();i++)out.append(String.format("%-"+(widths[i]+2)+"s",cells.get(i)));out.append("\n");}
    @Override public String finish(){return out.toString().stripTrailing();}
}
