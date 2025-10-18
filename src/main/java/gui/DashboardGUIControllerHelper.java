package gui;

import dao.DashboardDAO;
import modelo.PDIDashItem;
import modelo.Usuario;

import java.util.Collections;
import java.util.List;

/**
 * Classe de apoio (Helper) para o DashboardGUIController.
 * É responsável por isolar a lógica de acesso a dados (DAO) e a regra de negócio
 * para o carregamento inicial da lista de PDIs, baseando-se no tipo de acesso do usuário.
 */
public class DashboardGUIControllerHelper {

    private final DashboardDAO dao;

    public DashboardGUIControllerHelper() {
        this.dao = new DashboardDAO();
    }

    /**
     * Carrega a lista de itens PDI para exibição no Dashboard,
     * aplicando filtros de acesso de acordo com o tipo de usuário logado.
     * * @param usuario O usuário logado, contendo ID e tipo de acesso.
     * @return Lista de PDIDashItem.
     */
    public List<PDIDashItem> carregarDados(Usuario usuario) {
        if (usuario == null || usuario.getTipoAcesso() == null) {
            return Collections.emptyList();
        }

        // Garante que a comparação seja case-insensitive (RH, rh, Rh)
        String tipoAcesso = usuario.getTipoAcesso().toUpperCase().replace(" ", "");

        switch (tipoAcesso) {
            case "RH":
            case "GESTORGERAL":
                // RH e Gestor Geral veem a lista completa de PDIs.
                return dao.buscarPDIsRH();

            case "GESTORDEAREA":
                // Gestor de Área vê apenas os PDIs dos colaboradores sob sua gestão.
                // O método aqui é 'buscarPDIsGestorArea' conforme seu DAO.
                return dao.buscarPDIsGestorArea(usuario.getId());

            case "COLABORADOR":
                // Colaborador vê apenas seus próprios PDIs.
                // O método aqui é 'listarPDIColaborador' conforme seu DAO.
                return dao.listarPDIColaborador(usuario.getId());

            default:
                // Caso o tipo de acesso seja desconhecido
                return Collections.emptyList();
        }
    }
}