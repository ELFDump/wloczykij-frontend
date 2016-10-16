package pl.elfdump.wloczykij;

import pl.elfdump.wloczykij.network.APIManager;

public class Wloczykij {

    public static void onStart(){
        APIManager.setup(new Session());
    }

}
