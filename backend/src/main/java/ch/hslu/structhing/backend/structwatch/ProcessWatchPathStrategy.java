package ch.hslu.structhing.backend.structwatch;

public interface ProcessWatchPathStrategy {
    ProcessWatchPathType getType();
    void process();

}
