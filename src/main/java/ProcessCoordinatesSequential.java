package main.java;

import main.java.algorithm.GeneticAlgorithmPathFindingV2;

import java.io.*;
import java.util.*;
 /*
 * v1 implementação serial
 * */
public class ProcessCoordinatesSequential {

    public static void calculateBestRoutes(String filePath) {
        List<double[]> currentGroup = new ArrayList<>();
        int groupCount = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;

            while ((line = br.readLine()) != null) {
                line = line.trim();

                if (line.equals("-")) {
                    if (!currentGroup.isEmpty()) {
                        groupCount++;
                        System.out.println(groupCount);
                        List<double[]> bestRoute = GeneticAlgorithmPathFindingV2.geneticAlgorithm(currentGroup, 10, 10);
                        double distance = GeneticAlgorithmPathFindingV2.totalDistance(bestRoute);
                        saveRouteToFile(distance, groupCount);
                        currentGroup.clear();
                    }
                    continue;
                }

                String[] parts = line.split(";");
                if (parts.length != 2) continue;

                try {
                    double latitude = Double.parseDouble(parts[0].replace(",", "."));
                    double longitude = Double.parseDouble(parts[1].replace(",", "."));

                    currentGroup.add(new double[]{latitude, longitude});
                } catch (NumberFormatException e) {
                    System.err.println("Erro ao converter coordenadas: " + Arrays.toString(parts) + e);
                }
            }

            // Adiciona o último grupo se não estiver vazio
            if (!currentGroup.isEmpty()) {
                groupCount++;
                List<double[]> bestRoute = GeneticAlgorithmPathFindingV2.geneticAlgorithm(currentGroup, 10, 10);
                double distance = GeneticAlgorithmPathFindingV2.totalDistance(bestRoute);
                saveRouteToFile(distance, groupCount);
                //System.out.println(bestRoute);
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

    public static void main(String[] args) {
        String filePath = "C:\\Users\\amand\\code\\GeneticAlgorithm\\coordenadas_1000_50.txt";
        long start = System.nanoTime();
        calculateBestRoutes(filePath);
        long end = System.nanoTime();

        System.out.println("Tempo de carregamento dos dados: " + (end - start) / 1_000_000 + " ms");

    }
}
