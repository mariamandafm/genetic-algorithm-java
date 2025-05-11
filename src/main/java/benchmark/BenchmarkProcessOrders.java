package main.java.benchmark;
import main.java.GeneticAlgorithmPathFindingV1;
import main.java.GeneticAlgorithmPathFindingV2;
import main.java.ProcessOrders;
import main.java.ProcessOrdersV2;
import org.openjdk.jmh.annotations.*;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Thread)
public class BenchmarkProcessOrders {
    private String filePath;
    private String cityFilter;

    List<double[]> coordenadas;

    int generations = 10;

    int populationSize = 10;

    double[] start = {0.0, 0.0};

    @Setup(Level.Trial)
    public void setup() throws ExecutionException, InterruptedException {
        filePath = "pedidos_entrega.csv";
        cityFilter = "Natal";
        coordenadas = ProcessOrdersV2.getCoordinatesFromCSV(filePath, cityFilter);
    }

//    @Benchmark
//    public List<double[]> benchmarkProcessOrders() throws IOException {
//        return ProcessOrders.getCoordinatesFromCSV(filePath, cityFilter);
//    }
//
//    @Benchmark
//    public List<double[]> benchmarkProcessOrdersV2() throws IOException, ExecutionException, InterruptedException {
//        return ProcessOrdersV2.getCoordinatesFromCSV(filePath, cityFilter);
//    }

//    @Benchmark
//    public List<double[]> benchmarkGeneticAlgorithmV1(){
//        return GeneticAlgorithmPathFindingV1.geneticAlgorithm(coordenadas, start, generations, populationSize);
//    }

    @Benchmark
    public List<double[]> benchmarkGeneticAlgorithmV2() throws ExecutionException, InterruptedException {
        return GeneticAlgorithmPathFindingV2.geneticAlgorithm(coordenadas, start, generations, populationSize);
    }
}
