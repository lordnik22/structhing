package ch.hslu.structhing.backend.api.model;
import java.sql.Timestamp;
import ch.hslu.structhing.backend.structwatch.ProcessWatchPathType;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class StructWatchPath implements Serializable {

    private UUID id;
    private String directoryPath;
    private boolean initalProcessFlag;

    private Timestamp createTimestamp;

    private ProcessWatchPathType strategyType;
    private StructWatchPath() {
        // default constructor for jaxxon
    }

    public StructWatchPath(String directoryPath, boolean initalProcessFlag, Timestamp createTimestamp, ProcessWatchPathType strategyType){
        this.directoryPath = directoryPath;
        this.initalProcessFlag = initalProcessFlag;
        this.createTimestamp = createTimestamp;
        this.strategyType = strategyType;
    }

    public UUID getId() { return id; }
    public String getDirectoryPath() {
        return directoryPath;
    }
    public Timestamp getCreateTimestamp() {
        return createTimestamp;
    }

    public ProcessWatchPathType getStrategyType() {
        return strategyType;
    }

    public boolean getInitalProcessFlag() {
        return initalProcessFlag;
    }
}
