package pl.elfdump.wloczykij.network.api;

import java.util.List;
import java.util.Map;

public class APIBadRequestException extends APIRequestException {

    private Map<String, List<String>> errors;

    public APIBadRequestException(Map<String, List<String>> errors){
        super(String.format("The server responded with error %d: %s", 400, "Bad Request"));
        this.errors = errors;
    }

    public Map<String, List<String>> getErrors(){
        return errors;
    }

}
