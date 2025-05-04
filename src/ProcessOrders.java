import java.io.*;
import java.util.*;

public class ProcessOrders {

    public static List<double[]> getCoordinatesFromCSV(String filePath, String cityFilter) {
        List<double[]> coordinatesList = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");

                if (parts.length < 10) continue; // há dados suficientes

                String city = parts[2].trim(); // cidade

                if (city.equalsIgnoreCase(cityFilter)) {
                    try {
                        double latitude = Double.parseDouble(parts[7] + "." + parts[8]);
                        double longitude = Double.parseDouble(parts[9] + "." + parts[10]);
                        coordinatesList.add(new double[]{latitude, longitude});
                    } catch (NumberFormatException e) {
                        System.err.println("Erro ao converter coordenadas: " + Arrays.toString(parts));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return coordinatesList;
    }

    public static void main(String[] args) {
        String filePath = "pedidos_entrega.csv";
        String city = "Natal";
        long startProcessOrders = System.nanoTime();
        List<double[]> coordinates = getCoordinatesFromCSV(filePath, city);
        long endProcessOrders = System.nanoTime();
        System.out.println("Tempo de carregamento dos dados: " + (endProcessOrders-startProcessOrders)/1_000_000 + " ms");
        System.out.println("Coordenadas encontradas para" + city + ": " + coordinates.size());
    }
}
