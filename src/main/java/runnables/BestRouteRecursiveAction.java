package main.java.runnables;

import main.java.algorithm.GeneticAlgorithmPathFindingV2;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.atomic.AtomicInteger;

public class BestRouteRecursiveAction extends RecursiveAction {
    private final List<List<double[]>> allGroups;
    private final AtomicInteger groupCount;
    private static final int THRESHOLD = 20;

    public BestRouteRecursiveAction(List<List<double[]>> allGroups, AtomicInteger groupCount) {
        this.allGroups = allGroups;
        this.groupCount = groupCount;
    }

    @Override
    protected void compute() {
        if (allGroups.size() > THRESHOLD){
            ForkJoinTask.invokeAll(createSubtask());
        } else {
            processing(allGroups);
        }
    }

    private List<BestRouteRecursiveAction> createSubtask() {
        List<BestRouteRecursiveAction> subtasks = new ArrayList<>();
        List<List<double[]>> partOne = allGroups.subList(0, allGroups.size()/2);
        List<List<double[]>> partTwo = allGroups.subList(allGroups.size()/2, allGroups.size());
        subtasks.add(new BestRouteRecursiveAction(partOne, groupCount));
        subtasks.add(new BestRouteRecursiveAction(partTwo, groupCount));

        return subtasks;
    }

    private void processing(List<List<double[]>> allGroups){
        for (List<double[]> group : allGroups){
            int groupNumber = groupCount.getAndIncrement();
            System.out.println(groupNumber);
            List<double[]> bestRoute = GeneticAlgorithmPathFindingV2.geneticAlgorithm(group, 10, 10);
            double distance = GeneticAlgorithmPathFindingV2.totalDistance(bestRoute);
            saveRouteToFile(distance, groupNumber);
        }
    }

    public static synchronized void saveRouteToFile(double distance, int groupNumber) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("distancias.txt", true))) {
            writer.write("Grupo " + groupNumber + ": " + distance);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Erro ao salvar distancias: " + e.getMessage());
        }
    }
}
