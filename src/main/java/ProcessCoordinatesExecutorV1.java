package main.java;

import main.java.runnables.BestRouteRunnable;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/*
 * Usa o Executor Framework
 * */
public class ProcessCoordinatesExecutorV1 {
    public static void calculateBestRoutes(String filePath) throws InterruptedException {
        List<double[]> currentGroup = new ArrayList<>();
        BlockingQueue<List<double[]>> fila = new LinkedBlockingQueue<>();
        AtomicInteger groupCount = new AtomicInteger();

        int availableProcessors = 10;
        ExecutorService executor = Executors.newFixedThreadPool(availableProcessors);

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;

            while ((line = br.readLine()) != null) {
                line = line.trim();

                if (line.equals("-")) {
                    if (!currentGroup.isEmpty()) {
                        List<double[]> groupCopy = new ArrayList<>(currentGroup); // cópia segura
                        executor.execute(new BestRouteRunnable(groupCopy, groupCount));
                        currentGroup.clear();
                    }
                    continue;
                }
                addCoordinatePairToGroup(line, currentGroup);
            }
            if (!currentGroup.isEmpty()) {
                List<double[]> groupCopy = new ArrayList<>(currentGroup); // cópia segura
                executor.execute(new BestRouteRunnable(groupCopy, groupCount));
                currentGroup.clear();
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
        long start = System.nanoTime();
        calculateBestRoutes(filePath);
        long end = System.nanoTime();
        System.out.println("Tempo de carregamento dos dados: " + (end - start) / 1_000_000 + " ms");
    }
}
