package org.example.route;


import io.javalin.Javalin;
import org.example.service.Scanner;
import org.example.util.Utils;

public final class IndexRoute {
    public static final io.javalin.http.Handler HANDLER = context -> {
        try {
            int countThread = Utils.toInt(context.queryParam("count"));
            String result = new Scanner(countThread).findRange(context.queryParam("ip"));
            context.status(200);
            context.result("Ip scan successful. File name is " + result);
        } catch (IllegalArgumentException e) {
            context.result("Error in data: " + e.getMessage());
            context.status(406);
        } catch (RuntimeException e) {
            context.result("Error Server");
            context.status(500);
        }
    };

    public static void addRoutes(Javalin app) {
        app.post("/", HANDLER);
    }


}
