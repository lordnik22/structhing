package ch.hslu.structhing.backend.structwatch;

import ch.hslu.structhing.backend.api.model.StructWatchPath;
import ch.hslu.structhing.backend.jooq.generated.Tables;
import ch.hslu.structhing.backend.mapper.StructWatchMapper;
import org.jooq.DSLContext;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
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

    public void register(StructWatchPath dir) throws IOException {
        if (dir.getInitalProcessFlag()) {
            initalProcess(dir);
        }
        Path path = Path.of(dir.getDirectoryPath());
        WatchKey key = path.register(watchService, ENTRY_CREATE);
        dsl.insertInto(Tables.STRUCT_WATCH_PATH,
                        Tables.STRUCT_WATCH_PATH.INITAL_PROCESS_FLAG,
                        Tables.STRUCT_WATCH_PATH.DIRECTORY_PATH,
                        Tables.STRUCT_WATCH_PATH.WATCH_PATH_TYPE,
                        Tables.STRUCT_WATCH_PATH.WATCH_KEY)
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

            StructWatchPath structWatchPath = dsl.selectFrom(Tables.STRUCT_WATCH_PATH)
                    .where(Tables.STRUCT_WATCH_PATH.WATCH_KEY.eq(key.hashCode()))
                    .fetchSingle(r -> StructWatchMapper.toModel(r));
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
                dsl.delete(Tables.STRUCT_WATCH_PATH)
                        .where(Tables.STRUCT_WATCH_PATH.WATCH_KEY.eq(key.hashCode()))
                        .execute();

                // all directories are inaccessible
                if(!dsl.selectOne()
                        .from(Tables.STRUCT_WATCH_PATH)
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
