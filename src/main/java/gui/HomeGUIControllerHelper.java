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
 * É responsável por isolar a lógica de acesso a dados (DAO) e calcular os KPIs.
 */
public class HomeGUIControllerHelper {

    private final DashboardDAO dao;

    public HomeGUIControllerHelper() {
        this.dao = new DashboardDAO();
    }

    /**
     * Busca a lista base de PDIs com base no acesso do usuário.
     * Esta é a mesma lógica usada no DashboardControllerHelper.
     */
    private List<PDIDashItem> buscarListaBase(Usuario usuario) {
        if (usuario == null || usuario.getTipoAcesso() == null) {
            return Collections.emptyList();
        }

        String tipoAcesso = usuario.getTipoAcesso().toUpperCase();

        switch (tipoAcesso) {
            case "RH":
            case "GESTORGERAL":
                return dao.buscarPDIsRH();

            case "GESTORDEAREA":
                return dao.buscarPDIsGestorArea(usuario.getId());

            case "COLABORADOR":
                return dao.listarPDIColaborador(usuario.getId());

            default:
                return Collections.emptyList();
        }
    }

    /**
     * KPI 1: Retorna o total de PDIs que o usuário pode visualizar.
     * Para RH/Gestor Geral, retorna o total do sistema.
     * Para outros, retorna o total da lista de acesso.
     */
    public int getTotalPDIs(Usuario usuario) {
        // Se o usuário tem visão global, usa a contagem total do sistema
        if ("RH".equalsIgnoreCase(usuario.getTipoAcesso()) || "GESTORGERAL".equalsIgnoreCase(usuario.getTipoAcesso())) {
            return dao.contarTotalPDIs();
        }

        // Caso contrário, usa a contagem da lista de acesso do usuário (o número de PDIs que ele gerencia/possui)
        return buscarListaBase(usuario).size();
    }

    /**
     * KPI 2: Retorna a porcentagem de PDIs concluídos.
     * O cálculo é feito sobre o total que o usuário tem acesso (ou total do sistema).
     */
    public double getPorcentagemConcluidos(Usuario usuario) {
        int totalPDIs = getTotalPDIs(usuario);

        if (totalPDIs == 0) {
            return 0.0;
        }

        // --- Lógica Global (para RH/Gestor Geral) ---
        if ("RH".equalsIgnoreCase(usuario.getTipoAcesso()) || "GESTORGERAL".equalsIgnoreCase(usuario.getTipoAcesso())) {
            Map<String, Integer> contagem = dao.contarPDIsPorStatus();
            int concluidos = contagem.getOrDefault("Concluído", 0);

            // Note: O totalPDIs usado aqui é dao.contarTotalPDIs()
            return (double) concluidos * 100.0 / totalPDIs;
        }

        // --- Lógica Restrita (Gestor de Área/Colaborador) ---
        // Calcula o total de concluídos na lista restrita
        List<PDIDashItem> pdis = buscarListaBase(usuario);
        long concluidos = pdis.stream()
                .filter(pdi -> "CONCLUÍDO".equalsIgnoreCase(pdi.getStatus()))
                .count();

        return (double) concluidos * 100.0 / totalPDIs;
    }

    /**
     * KPI 3: Retorna o total de colaboradores cadastrados (apenas os com tipo_acesso='Colaborador').
     */
    public int getTotalColaboradores(Usuario usuario) {
        // Este KPI só deve ser visível para usuários com visão geral
        if ("RH".equalsIgnoreCase(usuario.getTipoAcesso()) || "GESTORGERAL".equalsIgnoreCase(usuario.getTipoAcesso())) {
            return dao.contarTotalColaboradores();
        }

        // Retorna 0 se o usuário não tem permissão para ver este KPI
        return 0;
    }

    /**
     * Retorna a contagem detalhada de PDIs por status.
     * Retorna a contagem global do sistema para RH/Gestor Geral.
     * TODO: Implementar a lógica de contagem restrita para outros tipos.
     */
    public Map<String, Integer> getDetalhesStatus(Usuario usuario) {
        // Retorna a contagem global do sistema
        if ("RH".equalsIgnoreCase(usuario.getTipoAcesso()) || "GESTORGERAL".equalsIgnoreCase(usuario.getTipoAcesso())) {
            return dao.contarPDIsPorStatus();
        }

        // Se for um usuário restrito, você pode criar uma contagem baseada na lista dele.
        // Por enquanto, retorna vazio para indicar que não há detalhe global.
        return Collections.emptyMap();
    }
}