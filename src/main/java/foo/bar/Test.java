package foo.bar;

import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.eclipse.microprofile.rest.client.RestClientDefinitionException;
import org.jboss.logging.Logger;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.Random;

import postman.com.Postman;
import postman.com.model.RequestDetails;


public class Test implements ITest {

    private static final Logger LOG = Logger.getLogger(Test.class);
    private static Random random = new Random();

    Postman postman = null;

    private String name;


    public Test(String name) {
        this.name = name;
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

    public String getName() { return this.name; }

    public Uni<String> test() {
        if(null == this.postman) {
            LOG.error("No client");
            return Uni.createFrom().failure(new RuntimeException("client"));
        }

        var result = Uni.createFrom().nullItem()
            .ifNoItem()
                .after(Duration.ofMillis(2000))
                .failWith(new RuntimeException("timeout"))
            .chain(unused -> {
                // Ignore initial dummy placeholder, start the actual processing here
                LOG.infof("Start test %s", this.name);
                return this.postman.echo();
            })
            .chain(info -> {
                // Got result of processing, turn it into what we have to return
                var testResult = random.nextBoolean();
                LOG.infof("Finished test %s (%s)", this.name, testResult ? "true" : "false");
                return testResult ? Uni.createFrom().item(this.name) : Uni.createFrom().nullItem();
            })
            .onFailure().invoke(e -> {
                LOG.errorf("Error in test %s: " + e, this.name);
            });

        return result;
    }

}
