package main.java.runnables;

import main.java.algorithm.GeneticAlgorithmPathFindingV2;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class BestRouteRunnable implements Runnable{
    private final List<double[]> coordinatesGroup;
    private final AtomicInteger groupCount;

    public BestRouteRunnable(List<double[]> coordinatesGroup, AtomicInteger groupCount) {
        this.coordinatesGroup = coordinatesGroup;
        this.groupCount = groupCount;
    }

    @Override
    public void run() {
        int groupNumber = groupCount.getAndIncrement();
        System.out.println(groupNumber);
        List<double[]> bestRoute = GeneticAlgorithmPathFindingV2.geneticAlgorithm(coordinatesGroup, 10, 10);
        double distance = GeneticAlgorithmPathFindingV2.totalDistance(bestRoute);
        saveRouteToFile(distance, groupNumber);
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
