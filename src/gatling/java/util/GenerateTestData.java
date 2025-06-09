package util;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;


public class GenerateTestData {

    private static final int NUMBER_OF_ASSOCIATES = 1_000_000;
    private static final String OUTPUT_FILE_PATH = "src/gatling/resources/gatling/data/associates.csv";

    public static void main(String[] args) {
        System.out.println("Iniciando a geração de dados de teste...");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(OUTPUT_FILE_PATH))) {
            writer.write("associateId");
            writer.newLine();

            for (int i = 1; i <= NUMBER_OF_ASSOCIATES; i++) {
                String associateId = String.format("cpf-%07d", i);
                writer.write(associateId);
                writer.newLine();
            }

            System.out.println("Arquivo " + OUTPUT_FILE_PATH + " gerado com sucesso com " + NUMBER_OF_ASSOCIATES + " registros.");

        } catch (IOException e) {
            System.err.println("Ocorreu um erro ao escrever o arquivo: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
