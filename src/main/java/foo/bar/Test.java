package foo.bar;

import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.eclipse.microprofile.rest.client.RestClientDefinitionException;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

import postman.com.Postman;
import postman.com.model.RequestDetails;


public class Test implements ITest {

    private static final Logger LOG = Logger.getLogger(Test.class);

    @RestClient
    Postman postman;


    public Test() {
        try {
            // Create the REST client for the transfer service
            URL url = new URL("https://postman-echo.com");
            this.postman = RestClientBuilder.newBuilder()
                            .baseUrl(url)
                            .build(Postman.class);
        }
        catch (MalformedURLException e) {
            LOG.error(e.getMessage());
        }
        catch (RestClientDefinitionException e) {
            LOG.error(e.getMessage());
        }
    }

    public Uni<Boolean> test1() {
        if(null == this.postman) {
            LOG.error("No client");
            return Uni.createFrom().failure(new RuntimeException("client"));
        }

        Uni<Boolean> result = Uni.createFrom().nullItem(); // <- This gets returned, handlers are not called

        result
            .ifNoItem()
                .after(Duration.ofMillis(2000))
                .failWith(new RuntimeException("timeout"))
            .chain(unused -> {
                // Ignore initial dummy placeholder, start the actual processing here
                LOG.info("Start processing");
                Uni<RequestDetails> info = this.postman.echo();
                return info;
            })
            .chain(s -> {
                // Got result of processing, turn it into what we have to return
                LOG.info("Finished processing");
                return Uni.createFrom().item(new Boolean(true));
            })
            .onFailure().invoke(e -> {
                LOG.error("Oops: " + e);
            });

        return result;
    }

    public Uni<Boolean> test2() {
        if(null == this.postman) {
            LOG.error("No client");
            return Uni.createFrom().failure(new RuntimeException("client"));
        }

        Uni<Boolean> result = Uni.createFrom().nullItem()
            .ifNoItem()
                .after(Duration.ofMillis(2000))
                .failWith(new RuntimeException("timeout"))
            .chain(unused -> {
                // Ignore initial dummy placeholder, start the actual processing here
                LOG.info("Start processing");
                Uni<RequestDetails> info = this.postman.echo();
                return info;
            })
            .chain(s -> {
                // Got result of processing, turn it into what we have to return
                LOG.info("Finished processing");
                return Uni.createFrom().item(new Boolean(true));
            })
            .onFailure().invoke(e -> {
                LOG.error("Oops: " + e);
            });

        return result;
    }

}
