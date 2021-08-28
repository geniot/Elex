package io.github.geniot.elex;

import com.sun.net.httpserver.HttpServer;
import io.github.geniot.elex.ezip.Logger;
import io.github.geniot.elex.handlers.*;
import io.github.geniot.elex.ui.ElexPreferences.Prop;

import java.net.InetSocketAddress;
import java.util.Observable;

import static io.github.geniot.elex.ui.ElexPreferences.get;
import static io.github.geniot.elex.ui.ElexPreferences.getInt;

public class ElexHttpServer extends Observable {

    private static ElexHttpServer instance;
    private HttpServer server;
    private Prop status;

    public static ElexHttpServer getInstance() {
        if (instance == null) {
            instance = new ElexHttpServer();
        }
        return instance;
    }

    private ElexHttpServer() {
    }

    public Prop getStatus() {
        return status;
    }


    public void start() {
        try {
            setStatus(Prop.STARTING);

            String host = get(Prop.HOST.name(), "localhost");
            int port = getInt(Prop.PORT.name(), 8000);

            server = com.sun.net.httpserver.HttpServer.create(new InetSocketAddress(host, port), 0);
            server.createContext("/", new StaticResourceHandler());
            server.createContext("/data", new ElexDataHandler());

            server.createContext("/css", new CssHandler());
            server.createContext("/icon", new IconHandler());
            server.createContext("/wav", new WavHandler());
            server.createContext("/img", new ImgHandler());
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
