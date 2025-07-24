package main.java.benchmark;

import main.java.benchmark.BenchmarkProcessOrders;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

public class BenchmarkRunner {
    public static void main(String[] args) throws Exception {
        Options opt = new OptionsBuilder()
                .include(BenchmarkProcessOrders.class.getName())
                .warmupIterations(5)
                .measurementIterations(20)
                .forks(1)
                .build();
        new Runner(opt).run();
    }
}
