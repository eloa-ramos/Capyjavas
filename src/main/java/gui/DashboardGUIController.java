package gui;

import dao.DashboardDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import modelo.PDIDashItem;
import modelo.Usuario;
import javafx.scene.control.TableRow;
import javafx.scene.control.Tooltip;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

public class DashboardGUIController {

    @FXML
    private TableView<PDIDashItem> tabela;

    @FXML
    private TableColumn<PDIDashItem, String> colColaborador;
    @FXML
    private TableColumn<PDIDashItem, String> colObjetivo;
    @FXML
    private TableColumn<PDIDashItem, String> colPrazo;
    @FXML
    private TableColumn<PDIDashItem, String> colStatus;
    @FXML
    private TableColumn<PDIDashItem, String> colArea;

    @FXML
    private ComboBox<String> filtroStatus;
    @FXML
    private ComboBox<String> filtroArea;

    @FXML
    private Button btnCadastroPdi;

    @FXML
    private ImageView logoImage;

    private ObservableList<PDIDashItem> listaOriginal;
    private Usuario usuarioLogado;
    private final DashboardDAO dao = new DashboardDAO();
    private final DateTimeFormatter dtFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public void setUsuario(Usuario usuario) {
        this.usuarioLogado = usuario;
        btnCadastroPdi.setVisible("RH".equalsIgnoreCase(usuario.getTipoAcesso()));

        carregarDados();
        configurarFiltros();
    }

    @FXML
    private void initialize() {
        // Colunas
        colColaborador.setCellValueFactory(new PropertyValueFactory<>("colaborador"));
        colObjetivo.setCellValueFactory(new PropertyValueFactory<>("objetivo"));
        colPrazo.setCellValueFactory(new PropertyValueFactory<>("prazo"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colArea.setCellValueFactory(new PropertyValueFactory<>("area"));
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        // Carregar logo
        try {
            Image img = new Image(getClass().getResourceAsStream("/gui/images/logo_youtan_transparente.png"));
            logoImage.setImage(img);
        } catch (Exception e) {
            System.err.println("Logo não encontrada: " + e.getMessage());
        }

        // Status colorido
        colStatus.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);
                    setTextFill(Color.WHITE);
                    switch (status) {
                        case "Concluído" -> setStyle("-fx-background-color: #4CAF50; -fx-alignment: CENTER;");
                        case "Em Andamento" -> setStyle("-fx-background-color: #FFC107; -fx-alignment: CENTER; -fx-text-fill: black;");
                        case "Atrasado" -> setStyle("-fx-background-color: #F44336; -fx-alignment: CENTER;");
                        default -> setStyle("");
                    }
                }
            }
        });

        // Tooltips e destaque de prazo
        tabela.setRowFactory(tv -> new TableRow<PDIDashItem>() {
            @Override
            protected void updateItem(PDIDashItem item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setTooltip(null);
                    setStyle("");
                } else {
                    Tooltip tooltip = new Tooltip("Objetivo: " + item.getObjetivo());
                    setTooltip(tooltip);

                    String prazoStr = item.getPrazo();
                    if (prazoStr != null && !prazoStr.isEmpty()) {
                        try {
                            LocalDate fim = LocalDate.parse(prazoStr, dtFormatter);
                            if (fim.isBefore(LocalDate.now().plusDays(7)) && fim.isAfter(LocalDate.now().minusDays(1))) {
                                setStyle("-fx-border-color: red; -fx-border-width: 0 0 2 0;");
                            } else {
                                setStyle("");
                            }
                        } catch (Exception e) {
                            setStyle("");
                        }
                    }
                }
            }
        });
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
        tabela.setItems(listaOriginal);

        filtroStatus.getItems().setAll("Todos", "Concluído", "Em Andamento", "Atrasado");
        filtroStatus.setValue("Todos");

        filtroArea.getItems().clear();
        filtroArea.getItems().add("Todas");
        dados.stream()
                .map(PDIDashItem::getArea)
                .distinct()
                .sorted()
                .forEach(area -> {
                    if (!area.isEmpty()) filtroArea.getItems().add(area);
                });
        filtroArea.setValue("Todas");
    }

    private void configurarFiltros() {
        filtroStatus.setOnAction(e -> aplicarFiltros());
        filtroArea.setOnAction(e -> aplicarFiltros());
    }

    private void aplicarFiltros() {
        if (listaOriginal == null) return;

        String statusSelecionado = filtroStatus.getValue();
        String areaSelecionada = filtroArea.getValue();

        List<PDIDashItem> filtrados = listaOriginal.stream()
                .filter(item -> statusSelecionado.equals("Todos") || item.getStatus().equals(statusSelecionado))
                .filter(item -> areaSelecionada.equals("Todas") || item.getArea().equals(areaSelecionada))
                .collect(Collectors.toList());

        tabela.setItems(FXCollections.observableArrayList(filtrados));
    }

    @FXML
    private void abrirCadastroPdi() {
        if (usuarioLogado != null && "RH".equalsIgnoreCase(usuarioLogado.getTipoAcesso())) {
            CadastroPdiWindow cadastro = new CadastroPdiWindow(usuarioLogado);
            cadastro.show();
        }
    }
}
