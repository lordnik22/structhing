package ch.hslu.structhing.backend.api.model;

class StructFilePath {
    private String filePath;
    private StructPathState state = StructPathState.INIT;

    private StructFilePath() {}

    public StructFilePath(String filePath) {
        this(filePath, StructPathState.INIT);
    }
    public StructFilePath(String filePath, StructPathState state) {
        this.filePath = filePath;
        this.state = state;
    }

    public String getFilePath() {
        return filePath;
    }

    public StructPathState getState() {
        return state;
    }
}
