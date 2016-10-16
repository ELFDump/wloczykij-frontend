package pl.elfdump.wloczykij.network;

public class APIResponse {

    private int statusCode;
    private String json;

    public APIResponse(int statusCode, String json){
        this.statusCode = statusCode;
        this.json = json;
    }

    public int code() {
        return statusCode;
    }

    public String json() {
        return json;
    }

}
