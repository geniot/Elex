package io.github.geniot.elex;

import ch.vorburger.exec.ManagedProcessException;
import ch.vorburger.mariadb4j.DB;
import ch.vorburger.mariadb4j.DBConfigurationBuilder;
import org.mariadb.jdbc.MariaDbPoolDataSource;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseServer {

    DB db;

    static MariaDbPoolDataSource pool;


    public static Connection getConnection() throws SQLException {
        if (pool == null) {
            try {
                pool = new MariaDbPoolDataSource("jdbc:mariadb://localhost:3306/elex?user=root&maxPoolSize=10&pool");
            } catch (SQLException e) {
                Logger.getInstance().log(e);
            }
        }
        return pool.getConnection();
    }

    public void start() {
        try {
            DBConfigurationBuilder configBuilder = DBConfigurationBuilder.newBuilder();
            configBuilder.setPort(3306); // OR, default: setPort(0); => autom. detect free port
            String dataDir = "data" + File.separator + "mariadb";
            if (System.getProperty("dataDir") != null) {
                dataDir = System.getProperty("dataDir");
            }
            configBuilder.setDataDir(dataDir); // just an example
            db = DB.newEmbeddedDB(configBuilder.build());
            db.start();
        } catch (ManagedProcessException e) {
            Logger.getInstance().log(e);
        }
    }

    public void stop() {
        try {
            if (db != null) {
                db.stop();
            }
        } catch (ManagedProcessException e) {
            Logger.getInstance().log(e);
        }
    }

    /**
     * In development mode we don't need to restart the database server every time with the http server.
     *
     * @param args
     */
    public static void main(String[] args) {
        new DatabaseServer().start();
    }
}
