package main.java;

import java.io.*;
import java.util.Random;

public class OrderDataGenerator {
    private static final long FILE_SIZE = 1L * 1024 * 1024 * 1024; // 1GB
    private static final String FILE_NAME = "pedidos_entrega.csv";

    private static final String[] NOMES = {"João Silva", "Maria Souza", "Carlos Oliveira", "Ana Santos", "Pedro Costa"};
    private static final String[] CIDADES = {"São Paulo", "Rio de Janeiro", "Belo Horizonte", "Porto Alegre", "Curitiba", "Brasília", "Fortaleza", "Manaus", "Salvador", "Natal", "Palmas", "São Luís","Campo Grande", "Apodi", "João Pessoa", "Parnamirim"};
    private static final String[] BAIRROS = {"Centro", "Jardins", "Vila Nova", "Liberdade", "Copacabana"};
    private static final String[] RUAS = {"Rua das Flores", "Avenida Brasil", "Rua XV de Novembro", "Avenida Paulista", "Rua da Praia"};

    public static void main(String[] args) {
        Random random = new Random();

        String header = "NomeCliente,Telefone,Cidade,CEP,Bairro,Rua,Numero,Latitude,Longitude\n";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            writer.write(header);
            long currentSize = header.getBytes().length;

            while (currentSize < FILE_SIZE) {
                String record = generateOrderRecord(random);
                writer.write(record);
                currentSize += record.getBytes().length;
            }

            System.out.println("Arquivo gerado: " + FILE_NAME);
            System.out.println("Tamanho final: " + (currentSize / (1024 * 1024)) + " MB");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String generateOrderRecord(Random random) {
        String nome = NOMES[random.nextInt(NOMES.length)];
        String telefone = generatePhoneNumber(random);
        String cidade = CIDADES[random.nextInt(CIDADES.length)];
        String cep = generateCEP(random);
        String bairro = BAIRROS[random.nextInt(BAIRROS.length)];
        String rua = RUAS[random.nextInt(RUAS.length)];
        int numero = random.nextInt(1000) + 1;

        double[] coords = getCityCoordinates(cidade);
        double latitude = coords[0] + (random.nextDouble() - 0.5) * 0.1;
        double longitude = coords[1] + (random.nextDouble() - 0.5) * 0.1;

        return String.format("%s,%s,%s,%s,%s,%s,%d,%.6f,%.6f\n",
                nome, telefone, cidade, cep, bairro, rua, numero, latitude, longitude);
    }

    private static String generatePhoneNumber(Random random) {
        return String.format("(%d) 9%d%d%d%d-%d%d%d%d",
                11 + random.nextInt(89), // DDD
                random.nextInt(10), random.nextInt(10), random.nextInt(10), random.nextInt(10),
                random.nextInt(10), random.nextInt(10), random.nextInt(10), random.nextInt(10));
    }

    private static String generateCEP(Random random) {
        return String.format("%d%d%d%d%d-%d%d%d",
                random.nextInt(10), random.nextInt(10), random.nextInt(10),
                random.nextInt(10), random.nextInt(10), random.nextInt(10),
                random.nextInt(10), random.nextInt(10));
    }

    private static double[] getCityCoordinates(String cidade) {
        switch (cidade) {
            case "São Paulo": return new double[]{-23.5505, -46.6333};
            case "Rio de Janeiro": return new double[]{-22.9068, -43.1729};
            case "Belo Horizonte": return new double[]{-19.9167, -43.9345};
            case "Porto Alegre": return new double[]{-30.0331, -51.2300};
            case "Curitiba": return new double[]{-25.4244, -49.2654};
            default: return new double[]{0, 0};
        }
    }
}