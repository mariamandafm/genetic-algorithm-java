package main.java;

import main.java.algorithm.GeneticAlgorithmPathFindingV2;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/*
 * v10 usa o Parallel Stream e coleta dos resultados no final
 * */
public class ProcessCoordinatesParallelStreamV2 {
    public static void calculateBestRoutes(String filePath) throws InterruptedException {
        List<List<double[]>> allGroups = new ArrayList<>();
        List<double[]> currentGroup = new ArrayList<>();
        int count = 0;

        // ler grupos
        readData(filePath, currentGroup, allGroups);

        // parallel stream
        List<Double> results = allGroups.parallelStream().map(group -> {
            List<double[]> bestRoute = GeneticAlgorithmPathFindingV2.geneticAlgorithm(group, 10, 10);
            double distance = GeneticAlgorithmPathFindingV2.totalDistance(bestRoute);
            return distance;
        }).collect(Collectors.toList());

        for (Double distance : results){
            int groupNumber = count++;
            System.out.println(groupNumber);
            saveRouteToFile(distance, groupNumber);
        }
    }

    public static void readData(String filePath, List<double[]> currentGroup, List<List<double[]>> allGroups) {
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
