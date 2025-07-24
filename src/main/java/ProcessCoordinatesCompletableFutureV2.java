package main.java;

import main.java.algorithm.GeneticAlgorithmPathFindingV2;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

/*
 * Usa o Completable Future
 * */
public class ProcessCoordinatesCompletableFutureV2 {
    public static void calculateBestRoutes(String filePath) throws InterruptedException {
        List<double[]> currentGroup = new ArrayList<>();

        List<CompletableFuture<Double>> completableFutures = new ArrayList<>();
        AtomicInteger groupCount = new AtomicInteger();

        // Ler grupos e criar futures
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();

                if (line.equals("-")) {
                    if (!currentGroup.isEmpty()) {
                        List<double[]> groupCopy = new ArrayList<>(currentGroup);
                        CompletableFuture<Double> completableFuture = CompletableFuture.supplyAsync(() -> {
                            List<double[]> bestRoute = GeneticAlgorithmPathFindingV2.geneticAlgorithm(groupCopy, 10, 10);
                            double distance = GeneticAlgorithmPathFindingV2.totalDistance(bestRoute);
                            return distance;
                        });
                        completableFutures.add(completableFuture);
                        currentGroup.clear();
                    }
                    continue;
                }
                addCoordinatePairToGroup(line, currentGroup);
            }
            if (!currentGroup.isEmpty()) {
                List<double[]> groupCopy = new ArrayList<>(currentGroup);
                CompletableFuture<Double> completableFuture = CompletableFuture.supplyAsync(() -> {
                    List<double[]> bestRoute = GeneticAlgorithmPathFindingV2.geneticAlgorithm(groupCopy, 10, 10);
                    double distance = GeneticAlgorithmPathFindingV2.totalDistance(bestRoute);
                    return distance;
                });
                completableFutures.add(completableFuture);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // coletar futures
        completableFutures.stream()
                .map(CompletableFuture::join)
                .forEach(distance -> {
                    int groupNumber = groupCount.getAndIncrement();
                    System.out.println(groupNumber);
                    saveRouteToFile(distance, groupNumber);
                });
    }

    private static void saveRouteToFile(double distance, int groupNumber) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("distancias.txt", true))) {
            writer.write("Grupo " + groupNumber + ": " + distance);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Erro ao salvar distancias: " + e.getMessage());
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
