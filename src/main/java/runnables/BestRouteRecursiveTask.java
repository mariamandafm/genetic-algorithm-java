package main.java.runnables;

import main.java.algorithm.GeneticAlgorithmPathFindingV2;

import java.util.List;
import java.util.concurrent.RecursiveTask;

public class BestRouteRecursiveTask extends RecursiveTask<Double> {
    private final List<double[]> coordinatesGroup;

    public BestRouteRecursiveTask(List<double[]> coordinatesGroup) {
        this.coordinatesGroup = coordinatesGroup;
    }

    @Override
    protected Double compute() {
        List<double[]> bestRoute = GeneticAlgorithmPathFindingV2.geneticAlgorithm(coordinatesGroup, 10, 10);
        return GeneticAlgorithmPathFindingV2.totalDistance(bestRoute);
    }
}
