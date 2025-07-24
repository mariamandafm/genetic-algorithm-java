package main.java;

import main.java.runnables.BestRouteCallable;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

/*
 * Use o Executor Framework com Callable a Future
 * */
public class ProcessCoordinatesExecutorV2 {
    public static void calculateBestRoutes(String filePath) throws InterruptedException {
        List<double[]> currentGroup = new ArrayList<>();
        BlockingQueue<List<double[]>> fila = new LinkedBlockingQueue<>();
        int groupCount = 0;

        int availableProcessors = 10;
        ExecutorService executor = Executors.newFixedThreadPool(availableProcessors);
        Map<Integer, Future<Double>> futures = new HashMap<>();


        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;

            while ((line = br.readLine()) != null) {
                line = line.trim();

                if (line.equals("-")) {
                    if (!currentGroup.isEmpty()) {
                        int groupNumber = groupCount++;
                        System.out.println(groupNumber);
                        List<double[]> groupCopy = new ArrayList<>(currentGroup);
                        Future<Double> future = executor.submit(new BestRouteCallable(groupCopy));
                        futures.put(groupNumber, future);

                        currentGroup.clear();
                    }
                    continue;
                }
                addCoordinatePairToGroup(line, currentGroup);
            }
            if (!currentGroup.isEmpty()) {
                int groupNumber = groupCount++;
                System.out.println(groupNumber);
                List<double[]> groupCopy = new ArrayList<>(currentGroup);
                Future<Double> future = executor.submit(new BestRouteCallable(groupCopy));
                futures.put(groupNumber, future);

                currentGroup.clear();
            }

            for (Integer key : futures.keySet()) {
                Future<Double> future = futures.get(key);
                try {
                    double distance = future.get(); // bloqueia só aqui, após todas as submissões
                    saveRouteToFile(distance, key);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            executor.shutdown();
            try{
                executor.awaitTermination(60, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
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
