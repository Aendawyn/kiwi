package fr.aquillet.kiwi.toolkit.lang;

public class Tuple<A, B> {

    private final A a;
    private final B b;

    private Tuple(A a, B b) {

        this.a = a;
        this.b = b;
    }

    public static <A, B> Tuple<A, B> tuple(A a, B b) {
        return new Tuple(a, b);
    }

    public A getA() {
        return a;
    }

    public B getB() {
        return b;
    }
}
