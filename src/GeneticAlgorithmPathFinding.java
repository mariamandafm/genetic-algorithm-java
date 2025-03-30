import javax.swing.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

class GeneticAlgorithmPathFinding {
    static Random random = new Random();

    // distância entre dois pontos com coordenadas double
    static double distance(double[] p1, double[] p2) {
        return Math.sqrt(Math.pow(p1[0] - p2[0], 2) + Math.pow(p1[1] - p2[1], 2));
    }

    // distância total
    static double totalDistance(List<double[]> route) {
        double sum = 0;
        for (int i = 0; i < route.size() - 1; i++) {
            sum += distance(route.get(i), route.get(i + 1));
        }
        return sum;
    }

    static List<List<double[]>> createPopulation(List<double[]> locations, double[] start, int size) {
        List<List<double[]>> population = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            System.out.println("Pop" + i);
            List<double[]> route = new ArrayList<>(locations);
            Collections.shuffle(route);
            route.add(0, start);
            route.add(start);
            population.add(route);
        }
        return population;
    }

    // (quanto maior, melhor)
    static double fitness(List<double[]> route) {
        return 1 / totalDistance(route);
    }

    static List<double[]> tournamentSelection(List<List<double[]>> population, int k) {
        List<List<double[]>> selected = new ArrayList<>();
        for (int i = 0; i < k; i++) {
            selected.add(population.get(random.nextInt(population.size())));
        }
        return selected.stream()
                .min(Comparator.comparingDouble(GeneticAlgorithmPathFinding::totalDistance))
                .orElse(null);
    }

    static List<double[]> crossover(List<double[]> parent1, List<double[]> parent2) {
        final int size = parent1.size();
        final Random random = new Random();

        int start = random.nextInt(size);
        int end = random.nextInt(size - start) + start;

        List<double[]> child = new ArrayList<>(size);

        Set<Integer> genesInChild = new HashSet<>(size);

        for (int i = start; i <= end; i++) {
            double[] gene = parent1.get(i);
            child.add(gene);
            genesInChild.add(Arrays.hashCode(gene));
        }

        int currentPos = 0;
        for (double[] gene : parent2) {
            if (currentPos == start) {
                currentPos = end + 1;  // Pula a região já copiada
                if (currentPos >= size) break;
            }

            int geneHash = Arrays.hashCode(gene);
            if (!genesInChild.contains(geneHash)) {
                child.add(currentPos, gene);
                genesInChild.add(geneHash);
                currentPos++;
            }
        }

        return child;
    }

    static List<double[]> mutate(List<double[]> route, double mutationRate) {
        if (random.nextDouble() < mutationRate) {
            int i = random.nextInt(route.size() - 2) + 1;
            int j = random.nextInt(route.size() - 2) + 1;
            Collections.swap(route, i, j);
        }
        if (random.nextDouble() < mutationRate / 2) {
            int i = random.nextInt(route.size() - 2) + 1;
            int j = random.nextInt(route.size() - 2) + 1;
            if (i > j) {
                int temp = i;
                i = j;
                j = temp;
            }
            Collections.reverse(route.subList(i, j));
        }
        return route;
    }

    static List<double[]> geneticAlgorithm(List<double[]> locations, double[] start,
                                           int generations, int populationSize) {
        System.out.println("Criando população");
        List<List<double[]>> population = createPopulation(locations, start, populationSize);
        System.out.println("População criada");

        for (int gen = 0; gen < generations; gen++) {
            System.out.println("Gen " + gen);
            List<List<double[]>> newPopulation = new ArrayList<>();

            for (int i = 0; i < populationSize / 2; i++) {
                newPopulation.add(tournamentSelection(population, 2));
            }

            List<List<double[]>> children = new ArrayList<>();
            for (int i = 0; i < populationSize - newPopulation.size(); i++) {
                List<double[]> parent1 = tournamentSelection(population, 2);
                List<double[]> parent2 = tournamentSelection(population, 2);
                List<double[]> child = crossover(parent1, parent2);
                children.add(mutate(child, 0.2));
            }
            newPopulation.addAll(children);

            population = newPopulation;
            System.out.println("Geração " + gen + " completada");
        }

        return population.stream()
                .min(Comparator.comparingDouble(GeneticAlgorithmPathFinding::totalDistance))
                .orElse(null);
    }

    public static void saveRouteToFile(List<double[]> route, String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (double[] point : route) {
                writer.write(String.format("[%.6f, %.6f]", point[0], point[1]));
                writer.newLine();
            }
            System.out.println("Rota salva em " + fileName);
        } catch (IOException e) {
            System.err.println("Erro ao salvar a rota: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        ProcessOrders processor = new ProcessOrders();
        String arquivoXML = "pedidos_entrega_mini.csv";
        String cidadeAlvo = "São Paulo";

        try {
            List<double[]> locations = ProcessOrders.getCoordinatesFromCSV(arquivoXML, cidadeAlvo);
            System.out.println("Dados carregados: " + locations.size() + " locais");

            double[] start = {0.0, 0.0};

            System.out.println("Iniciando algoritmo genético...");
            List<double[]> bestRoute = geneticAlgorithm(locations, start, 500, 200);
            System.out.println("Processamento finalizado");

            System.out.println("Distância total da melhor rota: " + totalDistance(bestRoute));
            saveRouteToFile(bestRoute, "melhor_rota.txt");
        } catch (Exception e) {
            System.err.println("Erro durante carregamento dos dados: " + e.getMessage());
            e.printStackTrace();
        }

    }
}