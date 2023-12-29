package ch.hslu.structhing.backend.api.model;

import ch.hslu.structhing.backend.structwatch.ProcessWatchPathType;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

public class StructWatchPath implements Serializable {

    private String directoryPath;
    private boolean initalProcessFlag;

    public LocalDateTime getCreateTimestamp() {
        return createTimestamp;
    }

    private LocalDateTime createTimestamp;

    private ProcessWatchPathType strategyType;
    private StructWatchPath() {
        // default constructor for jaxxon
    }

    public StructWatchPath(String directoryPath, boolean initalProcessFlag, LocalDateTime createTimestamp, ProcessWatchPathType strategyType){
        this.directoryPath = directoryPath;
        this.initalProcessFlag = initalProcessFlag;
        this.createTimestamp = createTimestamp;
        this.strategyType = strategyType;
    }

    public String getDirectoryPath() {
        return directoryPath;
    }

    public ProcessWatchPathType getStrategyType() {
        return strategyType;
    }

    public boolean getInitalProcessFlag() {
        return initalProcessFlag;
    }
}
