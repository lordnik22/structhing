package ch.hslu.structhing.backend;

import ch.hslu.structhing.backend.api.model.StructWatchPath;
import ch.hslu.structhing.backend.mapper.StructWatchMapper;
import ch.hslu.structhing.backend.structwatch.StructWatchService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import org.jooq.DSLContext;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;

import static ch.hslu.structhing.backend.jooq.generated.Tables.STRUCT_WATCH_PATH;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Application {
    private final StructWatchService watchService;
    private final ExecutorService pool;
    private final DSLContext dsl;
    private final Javalin api;

    private Application(StructWatchService watchService, ExecutorService pool, DSLContext dsl) {
        this.watchService = watchService;
        this.pool = pool;
        this.dsl = dsl;

        this.api = Javalin.create(config -> {
            config.staticFiles.add("/public", Location.CLASSPATH);
//            ObjectMapper mapper = new ObjectMapper();
//            mapper.registerModule(new JavaTimeModule());
//            config.jsonMapper(mapper);
        }).get("/api/watch/path/all", ctx -> {
            try {
                ctx.json(
                    dsl.selectFrom(STRUCT_WATCH_PATH)
                        .fetch(r -> StructWatchMapper.toModel(r))
                );
            } catch (DataAccessException e) {
                ctx.json(new ArrayList<StructWatchPath>());
            }
        }).put("/api/watch/path", ctx -> {
            StructWatchPath newWatchPath = ctx.bodyAsClass(StructWatchPath.class);
            watchService.register(newWatchPath);
        }).delete("/api/watch/path/{id}", ctx -> {
            UUID id = UUID.fromString(ctx.pathParam("id"));

            watchService.unregister(id);
        });
    }

    private void start() {
        pool.submit(() -> watchService.processEvents());
        pool.submit(() -> api.start(7070));
    }

    public static void main(String[] args)
            throws IOException {
        DSLContext dsl = DSL.using("jdbc:h2:./backend/src/main/resources/database/structhing", "sa", "");
        StructWatchService watchService1 = new StructWatchService(FileSystems.getDefault().newWatchService(), dsl);
        new Application(watchService1, Executors.newFixedThreadPool(2), dsl).start();
    }
}
