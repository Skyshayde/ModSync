package io.github.skyshayde;

import com.google.gson.Gson;
import org.eclipse.jetty.server.Server;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

class serverThread extends Thread {

    private Thread t;

    serverThread() {
    }

    public static void startServer() throws Exception {
        Server server = new Server(8080);
        server.setHandler(new mcServer());

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
