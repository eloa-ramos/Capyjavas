package gui;

import dao.DashboardDAO;
import modelo.PDIDashItem;
import modelo.Usuario;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Classe de apoio (Helper) para o HomeGUIController.
 * É responsável por isolar a lógica de acesso a dados (DAO) e calcular os KPIs,
 * aplicando o filtro de acesso (Global, Por Área ou Individual).
 */
public class HomeGUIControllerHelper {

    private final DashboardDAO dao = new DashboardDAO();

    public HomeGUIControllerHelper() {
        // Construtor limpo.
    }

    /**
     * Busca a lista base de PDIs com base no acesso do usuário.
     */
    private List<PDIDashItem> buscarListaBase(Usuario usuario) {
        if (usuario == null || usuario.getTipoAcesso() == null) {
            return Collections.emptyList();
        }

        String tipoAcesso = usuario.getTipoAcesso();

        switch (tipoAcesso) {
            case "RH":
                return dao.buscarPDIsRH();

            case "Gestor Geral":
                return dao.buscarPDIsGestorGeral();

            case "Gestor de Area":
                // Filtra pelos colaboradores que reportam ao ID do Gestor (ID do Usuário Logado)
                return dao.buscarPDIsGestorArea(usuario.getId());

            case "Colaborador":
                return dao.listarPDIColaborador(usuario.getId());

            default:
                return Collections.emptyList();
        }
    }

    /**
     * KPI 1: Retorna o total de PDIs que o usuário pode visualizar.
     */
    public int getTotalPDIs(Usuario usuario) {
        String tipoAcesso = usuario.getTipoAcesso();

        // Para RH/Gestor Geral, usa a contagem total do sistema
        if ("RH".equalsIgnoreCase(tipoAcesso) || "Gestor Geral".equalsIgnoreCase(tipoAcesso)) {
            return dao.contarTotalPDIs();
        }

        // Caso contrário, usa a contagem da lista de acesso do usuário (filtrada)
        return buscarListaBase(usuario).size();
    }

    /**
     * KPI 2: Retorna a porcentagem de PDIs concluídos.
     */
    public double getPorcentagemConcluidos(Usuario usuario) {
        int totalPDIs = getTotalPDIs(usuario);
        if (totalPDIs == 0) {
            return 0.0; // Retorna 0.0 se não há PDIs para evitar divisão por zero
        }

        long concluidos;
        String tipoAcesso = usuario.getTipoAcesso();

        if ("RH".equalsIgnoreCase(tipoAcesso) || "Gestor Geral".equalsIgnoreCase(tipoAcesso)) {
            Map<String, Integer> contagem = dao.contarPDIsPorStatus();
            concluidos = contagem.getOrDefault("Concluído", 0);
        } else {
            List<PDIDashItem> pdis = buscarListaBase(usuario);
            concluidos = pdis.stream()
                    // Usamos a string exata (CONCLUÍDO) para garantir correspondência com o DAO
                    .filter(pdi -> pdi.getStatus() != null && "CONCLUÍDO".equalsIgnoreCase(pdi.getStatus()))
                    .count();
        }

        return (double) concluidos * 100.0 / totalPDIs;
    }

    /**
     * KPI 3: Retorna o total de colaboradores.
     * Para Gestor de Área, conta colaboradores em sua área.
     */
    public int getTotalColaboradores(Usuario usuario) {
        String tipoAcesso = usuario.getTipoAcesso();

        if ("RH".equals(tipoAcesso) || "Gestor Geral".equals(tipoAcesso)) {
            return dao.contarTotalColaboradores();
        }

        if ("Gestor de Area".equals(tipoAcesso)) {
            // Conta os colaboradores da área onde o gestor está lotado.
            if (usuario.getIdArea() != null && usuario.getIdArea() > 0) {
                return dao.contarColaboradoresPorArea(usuario.getIdArea());
            }
        }

        return 0;
    }

    /**
     * Retorna a contagem detalhada de PDIs por status para o gráfico de pizza.
     */
    public Map<String, Integer> getDetalhesStatus(Usuario usuario) {
        String tipoAcesso = usuario.getTipoAcesso();

        if ("RH".equalsIgnoreCase(tipoAcesso) || "Gestor Geral".equalsIgnoreCase(tipoAcesso)) {
            return dao.contarPDIsPorStatus();
        }

        // Lógica para Gestor de Área/Colaborador
        List<PDIDashItem> pdis = buscarListaBase(usuario);

        return pdis.stream()
                .filter(pdi -> pdi.getStatus() != null && !pdi.getStatus().isBlank())
                .collect(Collectors.groupingBy(
                        PDIDashItem::getStatus,
                        Collectors.summingInt(p -> 1)
                ));
    }

    /**
     * Retorna a contagem de PDIs agrupados.
     * RH/Gestor Geral: Por Área.
     * Gestor de Área: Por Colaborador (Carga de Trabalho).
     */
    public Map<String, Integer> getContagemPdiPorArea(Usuario usuario) {
        String tipoAcesso = usuario.getTipoAcesso();

        if ("RH".equals(tipoAcesso) || "Gestor Geral".equals(tipoAcesso)) {
            return dao.contarPDIsPorArea();
        }

        if ("Gestor de Area".equals(tipoAcesso)) {
            List<PDIDashItem> pdis = buscarListaBase(usuario);

            // Agrupa pelo nome do Colaborador
            return pdis.stream()
                    .filter(pdi -> pdi.getColaborador() != null && !pdi.getColaborador().isBlank())
                    .collect(Collectors.groupingBy(
                            PDIDashItem::getColaborador,
                            Collectors.summingInt(p -> 1)
                    ));
        }

        return Collections.emptyMap();
    }
}