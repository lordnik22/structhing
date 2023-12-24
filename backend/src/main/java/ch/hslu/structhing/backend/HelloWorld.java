package ch.hslu.structhing.backend;

import ch.hslu.structhing.backend.jooq.generated.Tables;
import ch.hslu.structhing.backend.jooq.generated.tables.Test;
import ch.hslu.structhing.backend.jooq.generated.tables.records.TestRecord;
import ch.hslu.structhing.backend.model.StructFilePath;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.io.IOException;
import java.nio.file.*;
import java.util.Collections;

import static ch.hslu.structhing.backend.jooq.generated.tables.Test.TEST;

public class HelloWorld {
    static int iter = 0;
    static DSLContext dsl = DSL.using("jdbc:h2:./src/main/resources/database/structhing","sa","");
    public static void main(String[] args) {

        var app = Javalin.create(config -> {
            config.staticFiles.add("/public", Location.CLASSPATH);
        })
                .get("api/message", ctx -> {
                    dsl.insertInto(TEST, TEST.ID).values(iter++).execute();
                    TestRecord a = dsl.select(DSL.asterisk()).from(TEST)
                            .orderBy(TEST.ID.desc())
                            .limit(1)
                            .fetchOne(it -> it.into(TEST));
                    Integer id = a.getId();
                    ctx.json(Collections.singletonMap("message", "Greetings from Javalin Web Server. Database id-entry is: !" + id.toString()));
                }).start(7070);
    }

    public static void registerWatch() {
        WatchService watchService
                = null;
        try {
            watchService = FileSystems.getDefault().newWatchService();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Path path = Paths.get(System.getProperty("user.home"));

        try {
            path.register(
                    watchService,
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_DELETE,
                    StandardWatchEventKinds.ENTRY_MODIFY);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        WatchKey key;
        while (true) {
            try {
                if (!((key = watchService.take()) != null)) break;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            for (WatchEvent<?> event : key.pollEvents()) {
                System.out.println(
                        "Event kind:" + event.kind()
                                + ". File affected: " + event.context() + ".");
            }
            key.reset();
        }
    }
    }
}
