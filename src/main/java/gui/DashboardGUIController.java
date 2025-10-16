package gui;

import dao.DashboardDAO;
import modelo.PDIDashItem;
import modelo.Usuario;

import java.util.List;

/**
 * Classe de apoio para o DashboardGUIController, responsável por isolar a
 * lógica de acesso a dados (DAO) e de negócio relacionada ao carregamento
 * inicial da lista de PDIs.
 */
public class DashboardGUIControllerHelper {

    private final DashboardDAO dao;

    public DashboardGUIControllerHelper() {
        this.dao = new DashboardDAO();
    }

    /**
     * Carrega a lista de itens PDI para exibição no Dashboard,
     * aplicando filtros de acesso se necessário.
     * * @param usuario O usuário logado, usado para verificar o tipo de acesso.
     * @return Lista de PDIDashItem.
     */
    public List<PDIDashItem> carregarDados(Usuario usuario) {
        if ("RH".equalsIgnoreCase(usuario.getTipoAcesso())) {
            // Se for RH, carrega TODOS os PDIs
            return dao.listarTodosPDI();
        } else {
            // Se for Colaborador, carrega apenas os PDIs dele
            return dao.listarPDIColaborador(usuario.getId());
        }
    }
}