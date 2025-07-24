package main.java;

import main.java.runnables.BestRouteRecursiveAction;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/*
 * Usa o Fork/Join com Runnable
 * */
public class ProcessCoordinatesForkJoinV1 {
    public static void calculateBestRoutes(String filePath) throws InterruptedException {
        List<List<double[]>> allGroups = new ArrayList<>();
        List<double[]> currentGroup = new ArrayList<>();
        AtomicInteger groupCount = new AtomicInteger();

        ForkJoinPool forkJoinPool = new ForkJoinPool();

        // Ler grupos
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();

                if (line.equals("-")) {
                    if (!currentGroup.isEmpty()) {
                        allGroups.add(new ArrayList<>(currentGroup));
                        currentGroup.clear();
                    }
                    continue;
                }
                addCoordinatePairToGroup(line, currentGroup);
            }
            if (!currentGroup.isEmpty()) {
                allGroups.add(new ArrayList<>(currentGroup));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        forkJoinPool.invoke(new BestRouteRecursiveAction(allGroups, groupCount));
        forkJoinPool.shutdown();
        try{
            forkJoinPool.awaitTermination(60, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void addCoordinatePairToGroup(String line, List<double[]> currentGroup) {
        String[] parts = line.split(";");
        if (parts.length != 2) return;

        try {
            double latitude = Double.parseDouble(parts[0].replace(",", "."));
            double longitude = Double.parseDouble(parts[1].replace(",", "."));

            currentGroup.add(new double[]{latitude, longitude});
        } catch (NumberFormatException e) {
            System.err.println("Erro ao converter coordenadas: " + Arrays.toString(parts) + e);
        }
    }


    public static void main(String[] args) throws InterruptedException {
        String filePath = "C:\\Users\\amand\\code\\GeneticAlgorithm\\coordenadas_1000_50.txt";
        calculateBestRoutes(filePath);
    }
}
