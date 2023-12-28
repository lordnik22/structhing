package ch.hslu.structhing.backend;

import ch.hslu.structhing.backend.api.model.StructWatchPath;
import ch.hslu.structhing.backend.jooq.generated.Tables;
import ch.hslu.structhing.backend.mapper.StructWatchMapper;
import ch.hslu.structhing.backend.structwatch.StructWatchService;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.io.IOException;
import java.nio.file.*;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Logger;

import static ch.hslu.structhing.backend.jooq.generated.tables.StructWatchPath.STRUCT_WATCH_PATH;

public class Application {

    static ExecutorService pool;
    static DSLContext dsl = DSL.using("jdbc:h2:/home/lordnik/Documents/structhing/backend/src/main/resources/database/structhing","sa","");
    static StructWatchService watchService ;
    public static void main(String[] args) {
//        pool = Executors.newFixedThreadPool(2);
//        pool.submit(() -> StrucWatchService().processEvents());
//        pool.submit(() -> {
//            Javalin.create(config -> {
//                        config.staticFiles.add("/public", Location.CLASSPATH);
//                    })
//                    .put("/api/watch/path/{id}", ctx -> {
//                        StructWatchPath newWatchPath = ctx.bodyAsClass(StructWatchPath.class);
//                        watchService.register(newWatchPath);
//                    })
//                    .start(7070);
//        });

    }
}
