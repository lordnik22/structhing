package ch.hslu.structhing.backend.mapper;

import ch.hslu.structhing.backend.api.model.StructWatchPath;
import ch.hslu.structhing.backend.jooq.generated.tables.records.StructWatchPathRecord;
import ch.hslu.structhing.backend.structwatch.ProcessWatchPathType;

public class StructWatchMapper {

    public static StructWatchPath toModel(StructWatchPathRecord record) {
        StructWatchPath structWatchPath = new StructWatchPath(record.getDirectoryPath(),
                record.getInitalProcessFlag(),
                record.getCreateTimestamp(),
                ProcessWatchPathType.valueOf(record.getWatchPathType()));
        return structWatchPath;
    }
}
