package main.java.benchmark;

import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

public class BenchmarkRunner {
    public static void main(String[] args) throws Exception {
        Options opt = new OptionsBuilder()
                .include(BenchmarkProcessOrders.class.getSimpleName())
                .warmupIterations(2)
                .measurementIterations(10)
                .forks(1)
                .build();
        new Runner(opt).run();
    }
}
