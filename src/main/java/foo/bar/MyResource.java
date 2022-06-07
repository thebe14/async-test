package foo.bar;

import io.smallrye.mutiny.Uni;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.RestHeader;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;


@Path("/")
public class MyResource {

    private static final Logger LOG = Logger.getLogger(MyResource.class);

    private ITest tester;


    public MyResource() {
        this.tester = new Test();
    }

    @GET
    @Path("/test-one")
    public Response testOne() {

        AtomicReference<Response> result = new AtomicReference<>();
        Uni<String> start = Uni.createFrom().item("");
        start
            .chain(s -> {
                // Do test
                LOG.info("Start test");
                return tester.test1(); // <- Returns immediately, does not cause invocation of handlers in Test.test1()
            })
            .chain(b -> { // <- We get null passed to this handler
                // Success
                LOG.info("Finished test");
                result.set(Response.ok(b).build());
                return Uni.createFrom().nullItem();
            })
            .onFailure().invoke(e -> {
                LOG.error("Failed test");
                result.set(Response.ok().status(Status.EXPECTATION_FAILED).build());
            })
            .await().indefinitely();

        return result.get();
    }

    @GET
    @Path("/test-two")
    public Response testTwo() {

        AtomicReference<Response> result = new AtomicReference<>();
        Uni<String> start = Uni.createFrom().item("");
        start
                .chain(s -> {
                    // Do test
                    LOG.info("Start test");
                    return tester.test2();
                })
                .chain(b -> {
                    // Success
                    LOG.info("Finished test");
                    result.set(Response.ok(b).build());
                    return Uni.createFrom().nullItem();
                })
                .onFailure().invoke(e -> {
                    LOG.error("Failed test");
                    result.set(Response.ok().status(Status.EXPECTATION_FAILED).build());
                })
                .await().indefinitely();

        return result.get();
    }

}
