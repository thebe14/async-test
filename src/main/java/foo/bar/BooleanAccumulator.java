package foo.bar;

public class BooleanAccumulator {
    private boolean state;

    public BooleanAccumulator() {
        this.state = false;
    }

    public void accumulateAny(boolean b) {
        this.state = this.state || b;
    }

    public void accumulateAll(boolean b) {
        this.state = this.state && b;
    }

    public boolean get() { return this.state; }
}
