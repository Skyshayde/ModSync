package io.github.skyshayde;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;

class serverThread extends Thread {

    private Thread t;

    serverThread() {
    }

    public static void startServer() throws Exception {
        Server server = new Server(8080);
        ContextHandler mod_context = new ContextHandler("/mods");
        ContextHandler config_context = new ContextHandler("/config");

        ResourceHandler mods_handler = new ResourceHandler();
        ResourceHandler config_handler = new ResourceHandler();

        mods_handler.setResourceBase("./mods");
        config_handler.setResourceBase("./config");

        mods_handler.setDirAllowed(true);
        config_handler.setDirAllowed(true);

        mod_context.setHandler(mods_handler);
        config_context.setHandler(config_handler);

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[] { mod_context, config_context, new mcServer()});
        server.setHandler(handlers);

        server.start();
        server.join();
    }

    public void run() {
        try {
            startServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void start() {
        if (t == null) {
            t = new Thread(this);
            t.start();
        }
    }

}
