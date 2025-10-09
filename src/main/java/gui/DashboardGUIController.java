package gui;

import dao.DashboardDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import modelo.PDIDashItem;
import modelo.Usuario;

import java.util.List;
import java.util.stream.Collectors;

public class DashboardGUIController {

    @FXML private TableView<PDIDashItem> tabela;
    @FXML private TableColumn<PDIDashItem, String> colColaborador;
    @FXML private TableColumn<PDIDashItem, String> colObjetivo;
    @FXML private TableColumn<PDIDashItem, String> colPrazo;
    @FXML private TableColumn<PDIDashItem, String> colStatus;
    @FXML private TableColumn<PDIDashItem, String> colArea;

    @FXML private ComboBox<String> filtroStatus;
    @FXML private ComboBox<String> filtroArea;
    @FXML private Button btnCadastroPdi;
    @FXML private ImageView logoImage;

    private ObservableList<PDIDashItem> listaOriginal;
    private Usuario usuarioLogado;
    private final DashboardDAO dao = new DashboardDAO();

    public void setUsuario(Usuario usuario) {
        this.usuarioLogado = usuario;
        btnCadastroPdi.setVisible("RH".equalsIgnoreCase(usuario.getTipoAcesso()));
        carregarDados();
    }

    @FXML
    private void initialize() {
        // Configura colunas
        colColaborador.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getColaborador()));
        colObjetivo.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getObjetivo()));
        colPrazo.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getPrazo()));
        colStatus.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getStatus()));
        colArea.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getArea()));

        // Carrega logo
        try {
            Image img = new Image(getClass().getResourceAsStream("/gui/images/logo_youtan.png"));
            logoImage.setImage(img);
        } catch (Exception e) {
            System.err.println("Logo não encontrada: " + e.getMessage());
        }

        // Inicializa filtros
        filtroStatus.setOnAction(e -> aplicarFiltros());
        filtroArea.setOnAction(e -> aplicarFiltros());
    }

    private void carregarDados() {
        if (usuarioLogado == null) return;

        List<PDIDashItem> dados;
        switch (usuarioLogado.getTipoAcesso()) {
            case "RH" -> dados = dao.buscarPDIsRH();
            case "Gestor Geral" -> dados = dao.buscarPDIsGestorGeral();
            case "Gestor de Area" -> dados = dao.buscarPDIsGestorArea(usuarioLogado.getId());
            default -> dados = List.of();
        }

        listaOriginal = FXCollections.observableArrayList(dados);

        // Preenche combos dinamicamente
        filtroArea.getItems().clear();
        filtroArea.getItems().add("Todas");
        dados.stream()
                .map(PDIDashItem::getArea)
                .distinct()
                .sorted()
                .forEach(filtroArea.getItems()::add);

        filtroStatus.getItems().clear();
        filtroStatus.getItems().addAll("Todos", "Concluído", "Em Andamento", "Atrasado");

        filtroStatus.setValue("Todos");
        filtroArea.setValue("Todas");

        tabela.setItems(listaOriginal);
    }

    private void aplicarFiltros() {
        if (listaOriginal == null) return;

        String status = filtroStatus.getValue();
        String area = filtroArea.getValue();

        List<PDIDashItem> filtrados = listaOriginal.stream()
                .filter(item -> status.equals("Todos") || item.getStatus().equals(status))
                .filter(item -> area.equals("Todas") || item.getArea().equals(area))
                .collect(Collectors.toList());

        tabela.setItems(FXCollections.observableArrayList(filtrados));
    }

    @FXML
    private void abrirCadastroPdi() {
        CadastroPdiWindow cadastro = new CadastroPdiWindow(usuarioLogado);
        cadastro.show();
    }
}
