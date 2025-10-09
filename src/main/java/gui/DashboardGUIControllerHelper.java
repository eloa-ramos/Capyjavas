package gui;

import dao.DashboardDAO;
import modelo.PDIDashItem;
import modelo.Usuario;

import java.util.List;

public class DashboardGUIControllerHelper {

    private final DashboardDAO dao = new DashboardDAO();

    /**
     * Carrega os PDIs de acordo com o tipo de acesso do usuário logado.
     *
     * @param usuarioLogado Usuário logado
     * @return Lista de PDIDashItem
     */
    public List<PDIDashItem> carregarDados(Usuario usuarioLogado) {
        if (usuarioLogado == null) {
            return List.of();
        }

        String tipo = usuarioLogado.getTipoAcesso();

        switch (tipo) {
            case "RH":
                return dao.buscarPDIsRH();
            case "Gestor Geral":
                return dao.buscarPDIsGestorGeral();
            case "Gestor de Area":
                return dao.buscarPDIsGestorArea(usuarioLogado.getId());
            default:
                // Para colaboradores ou outros tipos sem dashboard
                return List.of(new PDIDashItem(
                        0,
                        "Sem Permissão",
                        "Acesso restrito",
                        "00/00/0000",
                        "Bloqueado",
                        ""
                ));
        }
    }
}
