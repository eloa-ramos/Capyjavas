package gui;

import modelo.Usuario;

// Esta classe será expandida para buscar os dados de KPIs no DAO.
public class HomeGUIControllerHelper {

    // Simulação de métodos para buscar KPIs
    public int getPDIAtivos(Usuario usuario) {
        // Lógica de DAO aqui
        return 15;
    }

    public int getTotalColaboradores() {
        // Lógica de DAO aqui
        return 30;
    }

    public double getPorcentagemConcluidos() {
        // Lógica de DAO aqui (ex: (total_concluido / total_pdi) * 100)
        return 45.5;
    }
}