package io.github.geniot.elex;


import java.util.prefs.Preferences;

public class ElexPreferences {

    public static String get(String name, String s) {
        return Preferences.userRoot().node(ElexApplication.class.getName()).get(name, s);
    }

    public static int getInt(String name, int i) {
        return Preferences.userRoot().node(ElexApplication.class.getName()).getInt(name, i);
    }

    public static boolean getBoolean(String name, boolean i) {
        return Preferences.userRoot().node(ElexApplication.class.getName()).getBoolean(name, i);
    }

    public static void putInt(String name, int i) {
        Preferences.userRoot().node(ElexApplication.class.getName()).putInt(name, i);
    }

    public static void pubBoolean(String name, boolean i) {
        Preferences.userRoot().node(ElexApplication.class.getName()).putBoolean(name, i);
    }

    public static void remove(String name) {
        Preferences.userRoot().node(ElexApplication.class.getName()).remove(name);
    }

    public static void put(String name, String path) {
        Preferences.userRoot().node(ElexApplication.class.getName()).put(name, path);
    }
}