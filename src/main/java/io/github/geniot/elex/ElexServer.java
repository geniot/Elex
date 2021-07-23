package io.github.geniot.elex;

import com.sun.net.httpserver.HttpServer;
import io.github.geniot.elex.handlers.*;

import java.net.InetSocketAddress;
import java.util.Observable;

public class ElexServer extends Observable {

    HttpServer server;
    Prop status;

    enum Prop {
        STARTING, STOPPING, STARTED, STOPPED, FAILED
    }


    public void start() {
        try {
            setStatus(Prop.STARTING);

            DictionariesPool.getInstance();
            String host = ElexPreferences.get(MainPanel.Prop.HOST.name(), "localhost");
            int port = ElexPreferences.getInt(MainPanel.Prop.PORT.name(), 8000);

            server = HttpServer.create(new InetSocketAddress(host, port), 0);
            server.createContext("/", new StaticResourceHandler());
            server.createContext("/languages", new LanguagesHandler());
            server.createContext("/dictionaries", new DictionariesHandler());
            server.createContext("/index", new IndexHandler());
            server.createContext("/icon", new IconHandler());
            server.setExecutor(null);
            server.start();

            setStatus(Prop.STARTED);
            String url = "http://" + host + ":" + port;
            Logger.getInstance().log("Started server on: " + url);
        } catch (Exception ex) {
            setStatus(Prop.FAILED);
            Logger.getInstance().log(ex);
        }
    }

    private void setStatus(Prop st) {
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
            Logger.getInstance().log("Stopped server in: " + t2 + " ms");
        } catch (Exception ex) {
            setStatus(Prop.FAILED);
            Logger.getInstance().log(ex);
        }
    }
}
