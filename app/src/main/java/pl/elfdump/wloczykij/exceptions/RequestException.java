package pl.elfdump.wloczykij.exceptions;

public class RequestException extends Exception {

    public RequestException(){
        super();
    }

    public RequestException(String message) {
        super(message);
    }

    public RequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public RequestException(Throwable cause) {
        super(cause);
    }

    public RequestException(int code, String error){
        super("Received API error response: " + error);
    }

}
