package edu.wotfk.intrigue;

public final class Conspiracy {
    public enum Status { PREPARED, ABANDONED, EXPOSED, EXECUTED, FAILED }
    private final int preparedRound;
    private Status status=Status.PREPARED;
    public Conspiracy(int preparedRound){this.preparedRound=preparedRound;}
    public int preparedRound(){return preparedRound;}
    public Status status(){return status;}
    public boolean prepared(){return status==Status.PREPARED;}
    public void finish(Status outcome){
        if(!prepared()||outcome==Status.PREPARED)throw new IllegalStateException("Conspiracy has already reached an outcome.");status=outcome;
    }
}
