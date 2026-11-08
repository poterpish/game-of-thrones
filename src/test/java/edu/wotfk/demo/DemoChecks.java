package edu.wotfk.demo;

import edu.wotfk.LaunchOptions;
import edu.wotfk.campaign.*;
import edu.wotfk.domain.*;
import edu.wotfk.ui.*;
import java.io.*;
import static edu.wotfk.testsupport.TestSupport.*;
public final class DemoChecks {
    private static String play(String scenario, long seed, String commands) throws Exception {
        StringWriter output = new StringWriter();
        ConsoleIO io = new ConsoleIO(new StringReader(commands), output);
        new ConsoleApplication(io, false, scenario, seed).run();
        return output.toString();
    }
    public static void main(String[] args) throws Exception {
        LaunchOptions options=LaunchOptions.parse(new String[]{"--scenario","battle","--seed","0","--color"});
        equal("battle",options.scenario(),"Scenario argument is validated");
        equal(0L,options.seed(),"A zero seed is preserved");
        check(options.color(),"Original optional color argument still works");
        rejects(()->LaunchOptions.parse(new String[]{"--scenario"}),"Missing scenario name is rejected");
        rejects(()->LaunchOptions.parse(new String[]{"--seed","oops"}),"Invalid seed is rejected");
        rejects(()->LaunchOptions.parse(new String[]{"--scenario","unknown"}),"Unknown scenario is rejected");
        for(String name:ScenarioDirector.NAMES){
            CampaignState state=new ScenarioDirector().create(name,0);
            check(!state.finished()&&state.map().territories().size()==12,"Demo "+name+" starts as an unfinished real campaign");
        }
        String commands="equip 1\ncommand\nbesiege TW\nend\nattack TW cautious\nmap\njournal\nquit\n";
        String first=play("battle",0,commands),second=play("battle",0,commands);
        check(first.equals(second),"Identical scenario, seed and command order reproduce the full output");
        check(first.contains("Stark captured TW"),"Battle demonstration reaches an actual capture");
        check(!first.contains("Not allowed:"),"Battle walkthrough contains no invalid command");
        String feast="feast Targaryen\nend\nplot 1\nend\nend\nfeasts\nquit\n";
        String success=play("feast",0,feast);
        check(success.contains("Private plan: EXECUTED"),"Full host walkthrough executes a prepared ambush");
        check(play("feast",3,feast).contains("Private plan: FAILED"),"A second verified seed produces a failed plot");
        check(play("feast",0,"feast Targaryen\nend\nend\nend\nquit\n").contains("honors guest right"),"A feast without a plot reaches a peaceful outcome");
        check(play("expose",0,"accept 1\nend\nspy Lannister\nexpose 1\nquit\n").contains("Conspiracy exposed"),"Player can discover an AI plot and prevent the ambush");
        check(play("victim",0,"accept 1\nmove TW\nend\nend\nend\nquit\n").contains("Stark loses"),"Ignoring the AI plot has real military consequences");
        String political=play("political",0,"endorse Targaryen\nendorse Baratheon\nend\n");
        check(political.contains("VICTORY")&&political.contains("recognized crown"),"Political demo earns endorsements through ordinary commands");
        check(play("military",0,"end\n").contains("military conquest"),"Military ending uses the ordinary end-round check");
        check(play("council",0,"end\n").contains("Great Council"),"Council demo reaches the normal deadline ending");
    }
}
