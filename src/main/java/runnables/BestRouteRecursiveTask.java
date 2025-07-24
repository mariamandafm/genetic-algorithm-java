package main.java.runnables;

import main.java.algorithm.GeneticAlgorithmPathFindingV2;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

public class BestRouteRecursiveTask extends RecursiveTask<List<Double>> {
    private final List<List<double[]>> allGroups;

    private static final int THRESHOLD = 20;

    public BestRouteRecursiveTask(List<List<double[]>> allGroups) {
        this.allGroups = allGroups;
    }

    @Override
    protected List<Double> compute() {
        if (allGroups.size() > THRESHOLD){
            List<BestRouteRecursiveTask> subtasks = createSubtask();
            invokeAll(subtasks);
            List<Double> combinedResults = new ArrayList<>();
            for (BestRouteRecursiveTask task : subtasks) {
                combinedResults.addAll(task.join()); // espera e coleta resultados de cada subtask
            }
            return combinedResults;
        } else {
            System.out.println("Processando....");
            return processing(allGroups);
        }
    }

    private List<BestRouteRecursiveTask> createSubtask() {
        List<BestRouteRecursiveTask> subtasks = new ArrayList<>();
        List<List<double[]>> partOne = allGroups.subList(0, allGroups.size()/2);
        List<List<double[]>> partTwo = allGroups.subList(allGroups.size()/2, allGroups.size());
        System.out.println("------------------------------------");
        System.out.println("Todos os grupos: " + allGroups.size());
        System.out.println("Parte 1: " + partOne.size());
        System.out.println("Parte 2: " + partTwo.size());

        subtasks.add(new BestRouteRecursiveTask(partOne));
        subtasks.add(new BestRouteRecursiveTask(partTwo));

        return subtasks;
    }

    private List<Double> processing(List<List<double[]>> allGroups){
        List<Double> distances = new ArrayList<>();
        for (List<double[]> group : allGroups){
            List<double[]> bestRoute = GeneticAlgorithmPathFindingV2.geneticAlgorithm(group, 10, 10);
            double distance = GeneticAlgorithmPathFindingV2.totalDistance(bestRoute);
            distances.add(distance);
        }
        return distances;
    }
}
