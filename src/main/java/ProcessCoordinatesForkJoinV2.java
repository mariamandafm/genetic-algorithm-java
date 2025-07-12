package main.java;

import main.java.runnables.BestRouteRecursiveTask;

import java.io.*;
import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.TimeUnit;

/*
 * Usa o Fork/Join com Callable
 * */
public class ProcessCoordinatesForkJoinV2 {
    public static void calculateBestRoutes(String filePath) throws InterruptedException {
        List<double[]> currentGroup = new ArrayList<>();
        int groupCount = 0;

        ForkJoinPool forkJoinPool = new ForkJoinPool();

        Map<Integer, ForkJoinTask<Double>> tasks = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;

            while ((line = br.readLine()) != null) {
                line = line.trim();

                if (line.equals("-")) {
                    if (!currentGroup.isEmpty()) {
                        int groupNumber = groupCount++;
                        System.out.println(groupNumber);
                        List<double[]> groupCopy = new ArrayList<>(currentGroup); // cópia segura
                        ForkJoinTask<Double> task = forkJoinPool.submit(new BestRouteRecursiveTask(groupCopy));
                        tasks.put(groupNumber, task);
                        currentGroup.clear();
                    }
                    continue;
                }
                addCoordinatePairToGroup(line, currentGroup);
            }
            if (!currentGroup.isEmpty()) {
                int groupNumber = groupCount++;
                System.out.println(groupNumber);
                List<double[]> groupCopy = new ArrayList<>(currentGroup); // cópia segura
                ForkJoinTask<Double> task = forkJoinPool.submit(new BestRouteRecursiveTask(groupCopy));
                tasks.put(groupNumber, task);
                currentGroup.clear();
            }

            for (Integer key : tasks.keySet()) {
                ForkJoinTask<Double> task = tasks.get(key);
                try {
                    double distance = task.get(); // bloqueia só aqui, após todas as submissões
                    saveRouteToFile(distance, key);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            forkJoinPool.shutdown();
            try{
                forkJoinPool.awaitTermination(60, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static synchronized void saveRouteToFile(double distance, int groupNumber) {
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
        long start = System.nanoTime();
        calculateBestRoutes(filePath);
        long end = System.nanoTime();
        System.out.println("Tempo de carregamento dos dados: " + (end - start) / 1_000_000 + " ms");
    }
}
