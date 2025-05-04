import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class ProcessOrdersV2 {

    public static List<double[]> getCoordinatesFromCSV(String filePath, String cityFilter) throws ExecutionException, InterruptedException {
        List<double[]> coordinatesList = new ArrayList<>();
        int numThreads = 4;
        File file = new File(filePath);
        long chunkSize = file.length()/numThreads;
        System.out.println(chunkSize);

        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        List<Future<List<double[]>>> coordFutures = new ArrayList<>();
        for (int i = 0; i < numThreads; i++){
            long start = i * chunkSize;
            long end = (i == numThreads - 1) ? file.length() : (i + 1) * chunkSize;
            Future<List<double[]>> future = executor.submit(() -> {
                List<double[]> coordinatesBatchList = new ArrayList<>();
                try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
                    br.skip(start);
                    long currentPosition = start;
                    String line;

                    while (currentPosition < end && (line = br.readLine()) != null) {
                        String[] parts = line.split(",");

                        if (parts.length < 10) continue; // há dados suficientes

                        String city = parts[2].trim(); // cidade

                        if (city.equalsIgnoreCase(cityFilter)) {
                            try {
                                double latitude = Double.parseDouble(parts[7] + "." + parts[8]);
                                double longitude = Double.parseDouble(parts[9] + "." + parts[10]);
                                coordinatesBatchList.add(new double[]{latitude, longitude});
                            } catch (NumberFormatException e) {
                                System.err.println("Erro ao converter coordenadas: " + Arrays.toString(parts));
                            }
                        }
                        currentPosition += line.length() + 1;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return coordinatesBatchList;
            });
            coordFutures.add(future);
        }
        for (Future<List<double[]>> future : coordFutures) {
            coordinatesList.addAll(future.get()); // Espera cada tarefa finalizar
        }
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES); // Garante o fim da execução
        return coordinatesList;
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        String filePath = "pedidos_entrega.csv";
        String city = "Natal";
        long startProcessOrders = System.nanoTime();
        List<double[]> coordinates = getCoordinatesFromCSV(filePath, city);
        long endProcessOrders = System.nanoTime();
        System.out.println("Tempo de carregamento dos dados: " + (endProcessOrders-startProcessOrders)/1_000_000 + " ms");
        System.out.println("Coordenadas encontradas para" + city + ": " + coordinates.size());
    }
}
