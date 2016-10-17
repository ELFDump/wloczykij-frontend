package pl.elfdump.wloczykij.network;

import pl.elfdump.wloczykij.Wloczykij;

public class APIManager {

    public static final String TAG = "Wloczykij";
    public static final String defaultServer = "http://dom.krzysh.pl:8000";

    public static APICall newCall(){
        return new APICall(Wloczykij.getSession());
    }

}
