package edu.wotfk.intrigue;

import edu.wotfk.domain.HouseId;
/** An invitation is a real scheduled meeting, not an instant army teleport. */
public final class Feast {
    public enum Status { INVITED, ACCEPTED, COMPLETED, CANCELLED }
    private final int id;
    private final HouseId host;
    private final HouseId guest;
    private final String castle;
    private final int invitedRound;
    private final int meetingRound;
    private Status status = Status.INVITED;
    public Feast(int id,HouseId host,HouseId guest,String castle,int round){
        this.id=id;this.host=host;this.guest=guest;this.castle=castle;this.invitedRound=round;this.meetingRound=round+2;
    }
    public int id(){return id;} public HouseId host(){return host;} public HouseId guest(){return guest;}
    public String castle(){return castle;} public int invitedRound(){return invitedRound;} public int meetingRound(){return meetingRound;}
    public Status status(){return status;} public boolean active(){return status==Status.INVITED||status==Status.ACCEPTED;}
    public void accept(){if(status!=Status.INVITED)throw new IllegalStateException("Invitation is not open.");status=Status.ACCEPTED;}
    public void complete(){if(!active())throw new IllegalStateException("Meeting has already ended.");status=Status.COMPLETED;}
    public void cancel(){if(active())status=Status.CANCELLED;}
}
