package main.java;

import java.util.*;

public class GeneticAlgorithmPathFindingV1 {
    static Random random = new Random();
    private static final Object fileLock = new Object();

    /*
    * Calcula a distância entre dois pontos.
    * */
    static double distance(double[] p1, double[] p2) {
        return Math.sqrt(Math.pow(p1[0] - p2[0], 2) + Math.pow(p1[1] - p2[1], 2));
    }

    /*
    * Distância total calculada como a soma das distância entre os pontos.
    * Função utilizada como fitness para decidir qual a melhor rota.
    * */
    public static double totalDistance(List<double[]> route) {
        double sum = 0;
        for (int i = 0; i < route.size() - 1; i++) {
            sum += distance(route.get(i), route.get(i + 1));
        }
        return sum;
    }

    /*
    * Cria população inicial gerando arrays cujos elementos são "genes" ordenados de forma aleatória.
    * */
    static List<List<double[]>> createPopulation(List<double[]> locations, int size) {
        List<List<double[]>> population = new ArrayList<>();
        for (int i = 0; i < size; i++) {
//            System.out.println("Pop" + i);
            List<double[]> route = new ArrayList<>(locations);
            Collections.shuffle(route);
            population.add(route);
        }
        return population;
    }

    /*
    * Seleciona um número k de membros da população e escolhe o que possui e menor distância.
    * */
    public static List<double[]> tournamentSelection(List<List<double[]>> population, int k) {
        List<List<double[]>> selected = new ArrayList<>();
        for (int i = 0; i < k; i++) {
            selected.add(population.get(random.nextInt(population.size())));
        }
        return selected.stream()
                .min(Comparator.comparingDouble(GeneticAlgorithmPathFindingV1::totalDistance))
                .orElse(null);
    }

    /*
    * Faz a troca de material genético com dois elementos, gerando um filho.
    * Esse é o método que mais exige processamento, pois ao copiar os genes do pai 2, é preciso checar se
    * o filho já não o herdou do pai 1.
    * */
    public static List<double[]> crossover(List<double[]> parent1, List<double[]> parent2) {
        final int size = parent1.size();
        final Random random = new Random();
        // Seleciona região aleatória para pegar os genes do pai 1.
        int start = random.nextInt(size);
        int end = random.nextInt(size - start) + start;

        List<double[]> child = new ArrayList<>(size);

        Set<Integer> genesInChild = new HashSet<>(size);

        for (int i = start; i <= end; i++) {
            double[] gene = parent1.get(i);
            child.add(gene);
            genesInChild.add(Arrays.hashCode(gene));
        }
        // Preenche o restante com os genes do pai 2.
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

    /*
    * Faz uma mutação no elemento.
    * mutationRate representa a probabilidade de ocorrer uma mutação.
    * */
    public static List<double[]> mutate(List<double[]> route, double mutationRate) {
        // Seleciona um número aleatório que deve estar abaixo da taxa de mutação
        if (random.nextDouble() < mutationRate) {
            // Troca dois genes
            int i = random.nextInt(route.size() - 2) + 1;
            int j = random.nextInt(route.size() - 2) + 1;
            Collections.swap(route, i, j);
        }
        if (random.nextDouble() < mutationRate / 2) {
            // Inverte um intervalo do gene
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

    public static List<double[]> geneticAlgorithm(List<double[]> locations,
                                           int generations, int populationSize) {
 //       System.out.println("Criando população");
        List<List<double[]>> population = createPopulation(locations, populationSize);
//        System.out.println("População criada");

        // Para cada geração
        for (int gen = 0; gen < generations; gen++) {
            //System.out.println("Gen " + gen);
            // Inicia uma nova população
            List<List<double[]>> newPopulation = new ArrayList<>();

            // Seleciona alguns individuos por torneio
            for (int i = 0; i < populationSize / 2; i++) {
                newPopulation.add(tournamentSelection(population, 2));
            }

            // Faz cruzamento entre elementos e mutação.
            List<List<double[]>> children = new ArrayList<>();
            for (int i = 0; i < populationSize - newPopulation.size(); i++) {
                List<double[]> parent1 = tournamentSelection(population, 2);
                List<double[]> parent2 = tournamentSelection(population, 2);
                List<double[]> child = crossover(parent1, parent2);
                children.add(mutate(child, 0.2));
            }
            newPopulation.addAll(children);

            population = newPopulation;
            //System.out.println("Geração " + gen + " completada");
        }

        // Por fim, seleciona o elemento com a melhor fitness (menor distância).
        return population.stream()
                .min(Comparator.comparingDouble(GeneticAlgorithmPathFindingV1::totalDistance))
                .orElse(null);
    }
}