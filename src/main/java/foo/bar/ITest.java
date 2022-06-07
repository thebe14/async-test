package foo.bar;

import io.smallrye.mutiny.Uni;


public interface ITest {

    public abstract Uni<Boolean> test1();
    public abstract Uni<Boolean> test2();
}
