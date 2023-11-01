package org.example;


import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import org.example.route.IndexRoute;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        Javalin app = Javalin.create(config -> {
            config.addStaticFiles("/forms", Location.CLASSPATH);
            config.enableDevLogging();
        }).start(7070); IndexRoute.addRoutes(app);
    }

}
