package main.java.benchmark;
import main.java.ProcessOrders;
import org.openjdk.jmh.annotations.*;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Thread)
public class Banchmark {
    private ProcessOrders po;
    private String filePath;
    private String cityFilter;

    @Setup(Level.Trial)
    public void setup() {
        filePath = "pedidos_entrega.csv";
        cityFilter = "Natal";
    }

//    @Benchmark
//    public List<double[]> benchmarkProcessOrders() throws IOException {
//        return ProcessOrders.getCoordinatesFromCSV(filePath, cityFilter);
//    }


}
