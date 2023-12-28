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
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import static ch.hslu.structhing.backend.jooq.generated.tables.StructWatchPath.STRUCT_WATCH_PATH;

public class Application {
    static int iter = 0;
    static DSLContext dsl = DSL.using("jdbc:h2:/home/lordnik/Documents/structhing/backend/src/main/resources/database/structhing","sa","");
    static StructWatchService watchService ;
//    public static void main(String[] args) {
//        Javalin.create(config -> {
//            config.staticFiles.add("/public", Location.CLASSPATH);
//        }).get("api/message", ctx -> {
//            dsl.insertInto(TEST, TEST.ID).values(iter++).execute();
//            TestRecord a = dsl.select(DSL.asterisk()).from(TEST)
//                    .orderBy(TEST.ID.desc())
//                    .limit(1)
//                    .fetchOne(it -> it.into(TEST));
//            Integer id = a.getId();
//            ctx.json(Collections.singletonMap("message", "Greetings from Javalin Web Server. Database id-entry is: !" + id.toString()));
//        })
//        .put("/api/watch/path/{id}", ctx -> {
//            StructWatchPath newWatchPath = ctx.bodyAsClass(StructWatchPath.class);
//            watchService.register(newWatchPath);
//        })
//        .start(7070);
//    }

    public static void main(String[] args) {
        dsl.select(Tables.STRUCT_WATCH_FILE.asterisk()).from(Tables.STRUCT_WATCH_FILE).fetch();
        System.out.println("yes we succeeded.");
    }
}
