package main.java;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ProcessCoordinatesVTeste {

    private static final int NUM_WORKERS = Runtime.getRuntime().availableProcessors();
    private static final List<double[]> POISON_PILL = List.of(); // sinal de término

    public static void calculateBestRoutes(String filePath) throws InterruptedException {
        BlockingQueue<List<double[]>> fila = new LinkedBlockingQueue<>();
        ExecutorService workers = Executors.newFixedThreadPool(NUM_WORKERS);

        // Worker threads (CPU-bound)
        for (int i = 0; i < NUM_WORKERS; i++) {
            int workerId = i + 1;
            workers.submit(() -> {
                try {
                    while (true) {
                        List<double[]> bloco = fila.take();

                        if (bloco == POISON_PILL) break;

                        List<double[]> bestRoute = GeneticAlgorithmPathFindingV2.geneticAlgorithm(bloco, 5, 4);
                        double distance = GeneticAlgorithmPathFindingV2.totalDistance(bestRoute);
                        GeneticAlgorithmPathFindingV2.saveRouteToFile(distance, workerId); // ID fictício
                        System.out.println("Worker " + workerId + " → Distância: " + distance);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        // Thread de leitura (I/O-bound)
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            List<double[]> grupo = new ArrayList<>();
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.equals("-")) {
                    if (!grupo.isEmpty()) {
                        fila.put(new ArrayList<>(grupo));
                        grupo.clear();
                    }
                } else {
                    String[] parts = line.split(";");
                    if (parts.length == 2) {
                        try {
                            double lat = Double.parseDouble(parts[0].replace(",", "."));
                            double lon = Double.parseDouble(parts[1].replace(",", "."));
                            grupo.add(new double[]{lat, lon});
                        } catch (NumberFormatException e) {
                            System.err.println("Erro ao converter coordenadas: " + line + " → " + e.getMessage());
                        }
                    }
                }
            }
            if (!grupo.isEmpty()) {
                fila.put(grupo);
            }

            // Enviar "sinais de fim" para encerrar os workers
            for (int i = 0; i < NUM_WORKERS; i++) {
                fila.put(POISON_PILL);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        workers.shutdown();
        workers.awaitTermination(10, TimeUnit.MINUTES);
    }

    public static void main(String[] args) throws InterruptedException {
        String filePath = "C:\\Users\\amand\\code\\GeneticAlgorithm\\coordenadas.txt";
        long start = System.nanoTime();
        calculateBestRoutes(filePath);
        long end = System.nanoTime();
        System.out.println("Tempo de execução: " + (end - start) / 1_000_000 + " ms");
    }
}
