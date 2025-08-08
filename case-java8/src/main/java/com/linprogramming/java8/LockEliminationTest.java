package com.linprogramming.java8;

import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

/**
 * @author LinShanglei
 */
@Fork(1)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 3)
@Measurement(iterations = 5)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class LockEliminationTest {
    static int x = 0;

    @Benchmark
    public void a() throws Exception {
        x++;
    }

    @Benchmark
    public void b() throws Exception {
        // JIT  即时编译器
        Object o = new Object();
        synchronized (o) {
            x++;
        }
    }
}
//Benchmark              Mode  Cnt  Score   Error  Units
//LockEliminationTest.a  avgt    5  0.313 ± 0.018  ns/op
//LockEliminationTest.b  avgt    5  0.313 ± 0.004  ns/op

