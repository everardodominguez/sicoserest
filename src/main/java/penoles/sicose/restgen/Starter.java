package penoles.sicose.restgen;

import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import penoles.sicose.restgen.model.db.DataBaseManager;

public class Starter {
    private static final Logger LOGGER = Logger.getLogger(Starter.class.getName());

    public static final String BASE_URI = "http://localhost/sicose";

    public static HttpServer startServer() {

        final ResourceConfig config = new ResourceConfig();

        config.register(MainResource.class);

        LOGGER.info("Starting Server........");

        final HttpServer httpServer = GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), config);

        return httpServer;

    }

    public static void main(String args[]) {
        if (args == null || args.length == 0) {
            LOGGER.log(Level.SEVERE, "Para iniciar es necesario proporcionar el nombre del servicio");
        } else {
            String serviceName = args[0];
            DataBaseManager.getInstance().setServiceName(serviceName);
            try {
                final HttpServer httpServer = startServer();

                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    try {
                        System.out.println("Shutting down the application...");

                        httpServer.shutdownNow();

                        System.out.println("Done, exit.");
                    } catch (Exception e) {
                        Logger.getLogger(Starter.class.getName()).log(Level.SEVERE, null, e);
                    }
                }));

                System.out.println(String.format("Application started.%nStop the application using CTRL+C"));

                Thread.currentThread().join();

            } catch (InterruptedException ex) {
                Logger.getLogger(Starter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
