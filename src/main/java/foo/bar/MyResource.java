package foo.bar;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.RestHeader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;


@Path("/")
public class MyResource {

    private static final Logger LOG = Logger.getLogger(MyResource.class);

    List<ITest> tests;

    public MyResource() {
        this.tests = Arrays.asList(
            new Test("one"),
            new Test("two"),
            new Test("three"),
            new Test("four"),
            new Test("five")
        );
    }

    @GET
    @Path("/pick-first")
    public Uni<Boolean> pickFirstValid() {

        //AtomicReference<Response> result = new AtomicReference<>();
        var result = Multi.createFrom().iterable(this.tests)

            .onItem().transformToUniAndConcatenate(test -> {
                // Do test
                LOG.infof("Calling test %s", test.getName());
                return test.test();
            })
            .onItem().transformToUniAndConcatenate(testResult -> {
                // Test ran successfully
                if(null != testResult && !testResult.isBlank()) {
                    LOG.infof("Found candidate (%s)", testResult);

                    // TODO: Cancel upstream
                }

                return Uni.createFrom().item(testResult);
            })
            .onFailure().invoke(e -> {
                LOG.error("Cannot pick first valid");
            })
            .collect()
            .in(() -> Boolean.FALSE, (acc, candidate) -> {
                acc = acc || (null != candidate && !candidate.isBlank());
            });

        return result;
    }

}
