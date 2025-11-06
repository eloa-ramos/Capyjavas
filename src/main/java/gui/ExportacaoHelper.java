package gui;

import javafx.scene.control.Alert;
import modelo.PDIDashItem;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Classe utilitária para lidar com a exportação de dados, como a geração de arquivos CSV.
 */
public class ExportacaoHelper {

    /**
     * Gera um arquivo CSV a partir de uma lista de PDIDashItem.
     *
     * @param pdis A lista de PDIs (o "endpoint" de dados).
     * @param arquivo O arquivo de destino (obtido pelo FileChooser).
     * @throws IOException Se ocorrer um erro de escrita.
     */
    public static void exportarListaParaCSV(List<PDIDashItem> pdis, File arquivo) throws IOException {

        // O "try-with-resources" garante que o writer será fechado.
        // Usamos UTF-8 com BOM para melhor compatibilidade com Excel.
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(arquivo, StandardCharsets.UTF_8))) {

            // Escreve o BOM (Byte Order Mark) para UTF-8, o Excel depende disso
            writer.write("\uFEFF");

            // 1. Criar o Cabeçalho (Header)
            String header = "\"ID_PDI\",\"Colaborador\",\"Area\",\"Objetivo_Skill\",\"Prazo_Final\",\"Status\"\n";
            writer.write(header);

            // 2. Iterar sobre os dados e escrever as linhas
            for (PDIDashItem item : pdis) {
                // Limpa os dados para evitar problemas com CSV (embora seja improvável neste dataset)
                String colaborador = escapeCsv(item.getColaborador());
                String area = escapeCsv(item.getArea() != null ? item.getArea() : "N/A");
                String objetivo = escapeCsv(item.getObjetivo() != null ? item.getObjetivo() : "");
                String prazo = escapeCsv(item.getPrazo());
                String status = escapeCsv(item.getStatus());

                String linha = String.format("\"%d\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"\n",
                        item.getIdPdi(),
                        colaborador,
                        area,
                        objetivo,
                        prazo,
                        status
                );
                writer.write(linha);
            }

            // 3. Salvar o arquivo
            writer.flush();
        }
    }

    /**
     * Método simples para "escapar" valores que possam quebrar o CSV.
     * Neste caso, apenas remove aspas duplas de dentro do texto.
     */
    private static String escapeCsv(String valor) {
        if (valor == null) {
            return "";
        }
        // Se o valor contiver aspas duplas, substitui por aspas simples
        return valor.replace("\"", "'");
    }
}