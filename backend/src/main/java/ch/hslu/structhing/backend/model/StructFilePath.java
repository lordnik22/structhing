package ch.hslu.structhing.backend.model;

import java.time.Instant;

public class StructFilePath {
    private String currentFileName;
    private String oldFileName;
    private StructPathState state = StructPathState.INIT;
    private StructFilePath() {}

    public StructFilePath(String filePath) {
        this(filePath, StructPathState.INIT);
    }
    public StructFilePath(String filePath, StructPathState state) {
        this.currentFileName = filePath;
        this.state = state;
    }

    public String getCurrentFileName() {
        return currentFileName;
    }

    public StructPathState getState() {
        return state;
    }
}
