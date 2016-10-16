package pl.elfdump.wloczykij.network;

import pl.elfdump.wloczykij.Session;

public class APIManager {

    public static final String TAG = "Wloczykij";
    public static final String defaultServer = "http://dom.krzysh.pl:8000";

    private static Session session;

    public static void setup(Session sessionNew){
        session = sessionNew;
    }

    public static APICall newCall(){
        return new APICall(session);
    }

    public static Session getSession(){
        return session;
    }

}
