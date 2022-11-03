package foo.bar;

import io.smallrye.mutiny.Uni;


public interface ITest {

    public abstract Uni<String> test();
    public abstract String getName();
}
