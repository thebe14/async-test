package postman.com;

import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import postman.com.model.RequestDetails;


/***
 * REST client for Postman
 */
@Path("/")
public interface Postman {

    @GET
    @Path("get")
    @Produces(MediaType.APPLICATION_JSON)
    Uni<RequestDetails> echo();
}
