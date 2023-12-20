package ch.hslu.structhing.backend;

import java.util.Collections;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;

public class HelloWorld {
    public static void main(String[] args) {
        var app = Javalin.create(config -> {
            config.staticFiles.add("/public", Location.CLASSPATH);
        })
                .get("api/message", ctx ->
                        ctx.json(Collections.singletonMap("message", "Greetings from Javalin Web Server!")))
                .start(7070);
    }
}
