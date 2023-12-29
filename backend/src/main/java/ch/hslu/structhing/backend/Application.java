package ch.hslu.structhing.backend;

import ch.hslu.structhing.backend.structwatch.StructWatchService;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Application {
    private final StructWatchService watchService;
    private final ExecutorService pool;
    private final DSLContext dsl;
    private final Javalin api;


    private Application(StructWatchService watchService, ExecutorService pool, DSLContext dsl, Javalin api) {
        this.watchService = watchService;
        this.pool = pool;
        this.dsl = dsl;
        this.api = api;
    }

    private void start() {
        pool.submit(() -> watchService.processEvents());
        pool.submit(() -> api.start(7070));
    }

    public static void main(String[] args)
            throws IOException {
        DSLContext dsl = DSL.using("jdbc:h2:/home/lordnik/Documents/structhing/backend/src/main/resources/database/structhing", "sa", "");
        StructWatchService watchService1 = new StructWatchService(FileSystems.getDefault().newWatchService(), dsl);
        Application app = new Application(
                watchService1,
                Executors.newFixedThreadPool(2),
                dsl,
                Javalin.create(config -> {
                        config.staticFiles.add("/public", Location.CLASSPATH);
                    })
                    .put("/api/watch/path/{id}", ctx -> {
                        // TODO Implement adding of new watch-paths
                        // StructWatchPath newWatchPath = ctx.bodyAsClass(StructWatchPath.class);
                        // watchService1.register(newWatchPath);
                    }));
        app.start();
    }
}
