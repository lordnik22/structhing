package ch.hslu.structhing.backend.api.model;

import ch.hslu.structhing.backend.structwatch.ProcessWatchPathType;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

public class EmptyStructWatchPath extends StructWatchPath {
    public EmptyStructWatchPath() {
        super(null, "", false, Timestamp.valueOf(LocalDateTime.now()), ProcessWatchPathType.PDF_ONLY);
    }
}
