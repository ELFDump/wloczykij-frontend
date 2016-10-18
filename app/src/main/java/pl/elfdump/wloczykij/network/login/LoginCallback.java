package pl.elfdump.wloczykij.network.login;

public interface LoginCallback {
    void success(String token);
    void failed();
}
