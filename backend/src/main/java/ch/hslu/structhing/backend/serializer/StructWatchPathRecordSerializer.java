package ch.hslu.structhing.backend.serializer;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import ch.hslu.structhing.backend.jooq.generated.tables.records.StructWatchPathRecord;

public class StructWatchPathRecordSerializer extends StdSerializer<StructWatchPathRecord> {

    public StructWatchPathRecordSerializer() {
        this(null);
    }

    protected StructWatchPathRecordSerializer(Class<StructWatchPathRecord> t) {
        super(t);
    }

    @Override
    public void serialize(
      StructWatchPathRecord value,
      JsonGenerator gen,
      com.fasterxml.jackson.databind.SerializerProvider provider
    ) throws IOException {
        gen.writeStartObject();
        gen.writeObjectField("id", value.getId());
        gen.writeObjectField("createTimestamp", value.getCreateTimestamp());
        gen.writeObjectField("directoryPath", value.getDirectoryPath());
        gen.writeObjectField("watchKey", value.getWatchKey());
        gen.writeObjectField("initialProcessFlag", value.getInitalProcessFlag());
        gen.writeObjectField("watchPathType", value.getWatchPathType());
        gen.writeEndObject();
    }
}
