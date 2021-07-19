package io.github.geniot.elex;

import com.sun.net.httpserver.HttpServer;
import io.github.geniot.elex.handlers.InfoHandler;
import io.github.geniot.elex.handlers.MyHandler;
import io.github.geniot.elex.handlers.ResourceHandler;

import java.net.InetSocketAddress;
import java.util.Observable;

public class ElexServer extends Observable {

    HttpServer server;
    Logger logger;
    Prop status;

    enum Prop {
        STARTING, STOPPING, STARTED, STOPPED, FAILED
    }

    public ElexServer(Logger l) {
        this.logger = l;
    }

    public void start() {
        try {
            setStatus(Prop.STARTING);
            String host = ElexPreferences.get(MainPanel.Prop.HOST.name(), "localhost");
            int port = ElexPreferences.getInt(MainPanel.Prop.PORT.name(), 8000);

            server = HttpServer.create(new InetSocketAddress(host, port), 0);
            server.createContext("/", new MyHandler(logger));
            server.createContext("/info", new InfoHandler(logger));
            server.createContext("/res", new ResourceHandler(logger));
            server.setExecutor(null); // creates a default executor
            server.start();

            setStatus(Prop.STARTED);
            String url = "http://" + host + ":" + port;
            logger.log("Started server on: " + url);
        } catch (Exception ex) {
            setStatus(Prop.FAILED);

            logger.log(ex);
        }
    }

    private void setStatus(Prop st){
        this.status = st;
        setChanged();
        notifyObservers();
    }

    public void stop() {
        try {
            setStatus(Prop.STOPPING);

            long t1 = System.currentTimeMillis();
            server.stop(0);

            setStatus(Prop.STOPPED);

            long t2 = System.currentTimeMillis() - t1;
            logger.log("Stopped server in: " + t2 + " ms");
        } catch (Exception ex) {
            setStatus(Prop.FAILED);

            logger.log(ex);
        }
    }
}
