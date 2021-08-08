package io.github.geniot.elex;

import com.sun.net.httpserver.HttpServer;
import io.github.geniot.elex.ElexPreferences.Prop;
import io.github.geniot.elex.handlers.CssHandler;
import io.github.geniot.elex.handlers.ElexDataHandler;
import io.github.geniot.elex.handlers.IconHandler;
import io.github.geniot.elex.handlers.StaticResourceHandler;

import java.net.InetSocketAddress;
import java.util.Observable;

import static io.github.geniot.elex.ElexPreferences.get;
import static io.github.geniot.elex.ElexPreferences.getInt;

public class ElexServer extends Observable {

    HttpServer server;
    Prop status;




    public void start() {
        try {
            setStatus(Prop.STARTING);

            String host = get(Prop.HOST.name(), "localhost");
            int port = getInt(Prop.PORT.name(), 8000);

            server = HttpServer.create(new InetSocketAddress(host, port), 0);
            server.createContext("/", new StaticResourceHandler());
            server.createContext("/data", new ElexDataHandler());

            server.createContext("/css", new CssHandler());
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
