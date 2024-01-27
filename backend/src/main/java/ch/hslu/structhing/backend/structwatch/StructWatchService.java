package ch.hslu.structhing.backend.structwatch;

import ch.hslu.structhing.backend.api.model.EmptyStructWatchPath;
import ch.hslu.structhing.backend.api.model.StructWatchPath;
import ch.hslu.structhing.backend.jooq.generated.Tables;
import ch.hslu.structhing.backend.mapper.StructWatchMapper;
import org.jooq.DSLContext;
import org.jooq.Result;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.jooq.Record;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Optional;
import java.util.UUID;

import static ch.hslu.structhing.backend.jooq.generated.Tables.STRUCT_WATCH_PATH;
import static java.nio.file.StandardWatchEventKinds.*;


public class StructWatchService {
    private final DSLContext dsl;
    private final WatchService watchService;
    @SuppressWarnings("unchecked")
    static <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>)event;
    }

    public StructWatchService(WatchService watchService, DSLContext dsl) {
        this.watchService = watchService;
        this.dsl = dsl;
    }

    public void unregister(UUID id) {
        dsl.deleteFrom(STRUCT_WATCH_PATH)
                .where(STRUCT_WATCH_PATH.ID.eq(id))
                .execute();
    }

    public void register(StructWatchPath dir) throws IOException {
        if (dir.getInitalProcessFlag()) {
            initalProcess(dir);
        }
        Path path = Path.of(dir.getDirectoryPath());
        WatchKey key = path.register(watchService, ENTRY_CREATE);

        dsl.insertInto(STRUCT_WATCH_PATH,
                        STRUCT_WATCH_PATH.INITAL_PROCESS_FLAG,
                        STRUCT_WATCH_PATH.DIRECTORY_PATH,
                        STRUCT_WATCH_PATH.WATCH_PATH_TYPE,
                        STRUCT_WATCH_PATH.WATCH_KEY)
                .values(dir.getInitalProcessFlag(),
                        dir.getDirectoryPath(),
                        dir.getStrategyType().toString(),
                        key.hashCode())
                .execute();
    }

    public void processEvents() {
        for (;;) {
            // wait for key to be signalled
            WatchKey key;
            try {
                key = watchService.take();
            } catch (InterruptedException x) {
                return;
            }

            Optional<StructWatchPath> checkStructWatchPath = dsl.selectFrom(STRUCT_WATCH_PATH)
                    .where(STRUCT_WATCH_PATH.WATCH_KEY.eq(key.hashCode()))
                    .fetchOptional(r -> StructWatchMapper.toModel(r));
            if (!checkStructWatchPath.isPresent()){
                key.cancel();
            }
            StructWatchPath structWatchPath = checkStructWatchPath.orElse(new EmptyStructWatchPath());
            Path dir = Path.of(structWatchPath.getDirectoryPath());
            if (dir == null) {
                System.err.println("WatchKey not recognized!!");
                continue;
            }

            for (WatchEvent<?> event: key.pollEvents()) {
                WatchEvent.Kind kind = event.kind();

                // TBD - provide example of how OVERFLOW event is handled
                if (kind == OVERFLOW) {
                    continue;
                }

                // Context for directory entry event is the file name of entry
                WatchEvent<Path> ev = cast(event);
                Path name = ev.context();
                Path child = dir.resolve(name);
                dispatchProcess(structWatchPath.getStrategyType(), child);
                System.out.format("%s: %s\n", event.kind().name(), child);
            }

            // reset key and remove from set if directory no longer accessible
            boolean valid = key.reset();
            if (!valid) {
                dsl.delete(STRUCT_WATCH_PATH)
                        .where(STRUCT_WATCH_PATH.WATCH_KEY.eq(key.hashCode()))
                        .execute();

                // all directories are inaccessible
                if(!dsl.selectOne()
                        .from(STRUCT_WATCH_PATH)
                        .fetchOptional()
                        .isPresent()) {
                    break;
                }
            }
        }
    }

    private void initalProcess(StructWatchPath start) throws IOException {
        Path startDir = Path.of(start.getDirectoryPath());
        Files.walkFileTree(startDir, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path dir, BasicFileAttributes attrs)
                    throws IOException
            {
            dispatchProcess(start.getStrategyType(), startDir.resolve(dir));
            return FileVisitResult.CONTINUE;
            }
        });
    }

    private void dispatchProcess(ProcessWatchPathType type, Path child) {
        switch (type) {
            case PDF_ONLY -> {
                new ProcessOnlyPdfStrategy(dsl, child).process();
            }
        }
    }
}
