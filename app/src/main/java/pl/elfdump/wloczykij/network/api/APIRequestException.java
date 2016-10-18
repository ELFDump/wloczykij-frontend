package pl.elfdump.wloczykij.network.api;

public class APIRequestException extends Exception {

    public APIRequestException(){
        super();
    }

    public APIRequestException(String message) {
        super(message);
    }

    public APIRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public APIRequestException(Throwable cause) {
        super(cause);
    }

}
