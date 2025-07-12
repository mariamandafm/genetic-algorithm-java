package main.java.benchmark;
import main.java.algorithm.GeneticAlgorithmPathFindingV2;
import main.java.algorithm.GeneticAlgorithmPathFindingV1;
import org.openjdk.jmh.annotations.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Thread)
public class BenchmarkProcessOrders {
    private List<double[]> individual1;
    private List<double[]> individual2;
    private List<double[]> individual3;
    private List<double[]> individual4;

    private final List<List<double[]>> population = new ArrayList<>();

    public static List<double[]> generateRandomCoordinates(int size) {
        List<double[]> coordinates = new ArrayList<>(size);
        Random random = new Random();

        for (int i = 0; i < size; i++) {
            // Gera latitude entre -90 e 90, longitude entre -180 e 180
            double latitude = -90 + 180 * random.nextDouble();
            double longitude = -180 + 360 * random.nextDouble();
            coordinates.add(new double[]{latitude, longitude});
        }

        return coordinates;
    }

    @Setup(Level.Trial)
    public void setup() throws ExecutionException, InterruptedException {
        individual1 = generateRandomCoordinates(50_000);
        individual2 = generateRandomCoordinates(50_000);
        individual3 = generateRandomCoordinates(50_000);
        individual4 = generateRandomCoordinates(50_000);
        population.add(individual1);
        population.add(individual2);
        population.add(individual3);
        population.add(individual4);
    }

    @Benchmark
    public void benchmarkCrossoverV1(){
        GeneticAlgorithmPathFindingV1.crossover(individual1, individual2);
    }

    @Benchmark
    public void benchmarkCrossoverV2(){
        GeneticAlgorithmPathFindingV2.crossover(individual1, individual2);
    }

    @Benchmark
    public void benchmarkTotalDistance(){
        GeneticAlgorithmPathFindingV1.totalDistance(individual1);
    }

    @Benchmark
    public void benchmarkTournamentSelection(){
        GeneticAlgorithmPathFindingV1.tournamentSelection(population, 2);
    }
    @Benchmark
    public void benchmarkMutate(){
        GeneticAlgorithmPathFindingV1.mutate(individual1, 0.2);
    }

    @Benchmark
    public void benchmarkGeneticAlgorithmV1(){
        GeneticAlgorithmPathFindingV1.geneticAlgorithm(individual1, 10, 10);
    }

    @Benchmark
    public void benchmarkGeneticAlgorithmV2(){
        GeneticAlgorithmPathFindingV2.geneticAlgorithm(individual1, 10, 10);
    }

}
// Benchmark
// crossover
// tournament
// totalDistance
// mutate
// geneticAlgorithm
// Leitura dos dados