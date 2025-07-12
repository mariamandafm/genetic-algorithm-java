package main.java.runnables;

import main.java.algorithm.GeneticAlgorithmPathFindingV2;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;

public class BestRouteCallable implements Callable<Double> {
    private final List<double[]> coordinatesGroup;

    public BestRouteCallable(List<double[]> coordinatesGroup) {
        this.coordinatesGroup = coordinatesGroup;
    }

    @Override
    public Double call() throws Exception {
        List<double[]> bestRoute = GeneticAlgorithmPathFindingV2.geneticAlgorithm(coordinatesGroup, 10, 10);
        return GeneticAlgorithmPathFindingV2.totalDistance(bestRoute);
    }

    private static synchronized void saveRouteToFile(double distance, int groupNumber) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("distancias.txt", true))) {
            writer.write("Grupo " + groupNumber + ": " + distance);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Erro ao salvar distancias: " + e.getMessage());
        }
    }
}
