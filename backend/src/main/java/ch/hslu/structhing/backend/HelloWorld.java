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
}
