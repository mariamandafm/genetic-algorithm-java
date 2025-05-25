package main.java;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/*
 * v3 usa apenas virtual threads.
 * */
public class ProcessCoordinatesV3 {
    public static void calculateBestRoutes(String filePath) throws InterruptedException {
        List<double[]> currentGroup = new ArrayList<>();
        BlockingQueue<List<double[]>> fila = new LinkedBlockingQueue<>();
        AtomicInteger groupCount = new AtomicInteger();
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(availableProcessors);

        Thread reader = getReader(filePath, currentGroup, fila, availableProcessors);

        for (int i =0; i < Runtime.getRuntime().availableProcessors(); i++){
            executor.submit(() -> {
                try {
                    while(true) {
                        List<double[]> bloco = fila.take();
                        if (bloco.isEmpty()) break;
                        int groupNumber = groupCount.getAndIncrement();
                        System.out.println(groupCount);

                        List<double[]> bestRoute = GeneticAlgorithmPathFindingV2.geneticAlgorithm(bloco, 10, 10);
                        double distance = GeneticAlgorithmPathFindingV2.totalDistance(bestRoute);
                        saveRouteToFile(distance, groupNumber);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
        reader.join(); // Esperar reader terminar
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.MINUTES);
    }

    private static synchronized void saveRouteToFile(double distance, int groupNumber) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("distancias.txt", true))) {
            writer.write("Grupo " + groupNumber + ": " + distance);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Erro ao salvar distancias: " + e.getMessage());
        }
    }

    @NotNull
    private static Thread getReader(String filePath, List<double[]> currentGroup, BlockingQueue<List<double[]>> fila, int availableProcessors) {

        return Thread.ofVirtual().start(() -> {
            try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
                String line;

                while ((line = br.readLine()) != null) {
                    line = line.trim();

                    if (line.equals("-")) {
                        if (!currentGroup.isEmpty()) {
                            fila.put(new ArrayList<>(currentGroup));
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
                if (!currentGroup.isEmpty()) {
                    fila.put(currentGroup);
                }
                for (int i = 0; i < availableProcessors; i++) {
                    fila.put(List.of()); // bloco vazio como "sentinela"
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    public static void main(String[] args) throws InterruptedException {
        String filePath = "C:\\Users\\amand\\code\\GeneticAlgorithm\\coordenadas_1000_50.txt";
        long start = System.nanoTime();
        calculateBestRoutes(filePath);
        long end = System.nanoTime();
        System.out.println("Tempo de carregamento dos dados: " + (end - start) / 1_000_000 + " ms");
    }
}
