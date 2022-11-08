package foo.bar;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.tuples.Tuple2;
import org.jboss.logging.Logger;
import org.reactivestreams.Subscription;

import java.util.*;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.*;


@RequestScoped
@Path("/")
public class MyResource {

    private static final Logger LOG = Logger.getLogger(MyResource.class);

    private List<ITest> tests;
    //private Map<String, Subscription> subscriptions;
    private Subscription subscription;

    public MyResource() {
        this.tests = Arrays.asList(
            new Test("one"),
            new Test("two"),
            new Test("three"),
            new Test("four"),
            new Test("five")
        );
    }

    private Uni<Boolean> pickFirstValidImpl() {

        // Create unique ID for this call
        String callId = UUID.randomUUID().toString();

        var result = Multi.createFrom().iterable(this.tests)

            .onSubscription().invoke(sub -> {
                // Save the subscription, so we can cancel later
                LOG.info("Subscribed");
                this.subscription = sub;
            })
            .onItem().transformToUniAndConcatenate(test -> {
                // Do test
                LOG.infof("Calling test %s", test.getName());
                return test.test();
            })
            .onItem().transformToUniAndConcatenate(testResult -> {
                // Test ran successfully
                if(null != testResult && !testResult.isBlank()) {
                    LOG.infof("Found candidate (%s)", testResult);

                    // Cancel upstream
                    if(null != this.subscription)
                        subscription.cancel();

                    return Uni.createFrom().item(true);
                }

                return Uni.createFrom().item(false);
            })
            .onFailure().invoke(e -> {
                LOG.error("Cannot pick first valid");
            })
            .collect()
            .in(BooleanAccumulator::new, (acc, candidate) -> {
                acc.accumulateAny(candidate);
            })
            .onItem().transform(acc -> acc.get());

        return result;
    }

    @GET
    @Path("/pick-first")
    public Uni<Boolean> pickFirstValid() {

        Uni<Boolean> result = pickFirstValidImpl()

            .chain(success -> {
                // Check result
                if(!success)
                    LOG.info("No valid candidate found");

                return Uni.createFrom().item(success);
            })
            .onFailure().invoke(e -> {
                LOG.error("Ooops!");
            });

        return result;
    }

}
