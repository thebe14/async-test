package postman.com.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;


/**
 * Details of an HTTP request
 */
//@JsonIgnoreProperties(ignoreUnknown = true)
public class RequestDetails {

    public String url;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public Map<String, String> headers;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public Map<String, String> args;

    public RequestDetails() {}
}
