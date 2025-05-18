package main.java;

import java.io.*;
import java.util.*;

public class ProcessOrders {

    public static void getCoordinatesFromCSV(String filePath) {
        List<List<double[]>> groupedCoordinates = new ArrayList<>();
        List<double[]> currentGroup = new ArrayList<>();
        int groupCount = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;

            while ((line = br.readLine()) != null) {
                line = line.trim();

                if (line.equals("-")) {
                    if (!currentGroup.isEmpty()) {
                        groupCount++;

                        List<double[]> bestRoute = GeneticAlgorithmPathFindingV1.geneticAlgorithm(currentGroup, 5, 4);
                        double distance = GeneticAlgorithmPathFindingV1.totalDistance(bestRoute);
                        GeneticAlgorithmPathFindingV1.saveRouteToFile(distance, groupCount);
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
                List<double[]> bestRoute = GeneticAlgorithmPathFindingV1.geneticAlgorithm(currentGroup, 5, 4);
                double distance = GeneticAlgorithmPathFindingV1.totalDistance(bestRoute);
                GeneticAlgorithmPathFindingV1.saveRouteToFile(distance, groupCount);
                //System.out.println(bestRoute);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        String filePath = "C:\\Users\\amand\\code\\GeneticAlgorithm\\coordenadas_500.txt";
        long start = System.nanoTime();
        getCoordinatesFromCSV(filePath);
        long end = System.nanoTime();

        System.out.println("Tempo de carregamento dos dados: " + (end - start) / 1_000_000 + " ms");
//        System.out.println("Total de grupos de coordenadas: " + groupedCoordinates.size());
//        for (int i = 0; i < groupedCoordinates.size(); i++) {
//            System.out.println("Grupo " + (i + 1) + ": " + groupedCoordinates.get(i).size() + " coordenadas");
//        }
    }
}
