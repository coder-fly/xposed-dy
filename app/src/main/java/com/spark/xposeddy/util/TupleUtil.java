package com.spark.xposeddy.util;


/**
 * 元组-一次方法调用返回多个对象
 */
public final class TupleUtil {
    private TupleUtil() {
    }

    public static <A, B> TwoTuple<A, B> tuple(A a, B b) {
        return new TwoTuple(a, b);
    }

    public static <A, B, C> ThreeTuple<A, B, C> tuple(A a, B b, C c) {
        return new ThreeTuple(a, b, c);
    }

    public static <A, B, C, D> FourTuple<A, B, C, D> tuple(A a, B b, C c, D d) {
        return new FourTuple(a, b, c, d);
    }

    public static class TwoTuple<A, B> {
        private final A first;
        private final B second;

        public TwoTuple(A a, B b) {
            this.first = a;
            this.second = b;
        }

        public A getFirst() {
            return this.first;
        }

        public B getSecond() {
            return this.second;
        }
    }

    public static class ThreeTuple<A, B, C> extends TwoTuple<A, B> {
        private final C third;

        public ThreeTuple(A a, B b, C c) {
            super(a, b);
            this.third = c;
        }

        public C getThird() {
            return this.third;
        }
    }

    public static class FourTuple<A, B, C, D> extends ThreeTuple<A, B, C> {
        private final D fourth;

        public FourTuple(A a, B b, C c, D d) {
            super(a, b, c);
            this.fourth = d;
        }

        public D getFourth() {
            return this.fourth;
        }
    }
}
