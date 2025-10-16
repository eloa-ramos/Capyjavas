package gui;

import dao.DashboardDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.CheckBox;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;
import javafx.scene.Scene;
import modelo.PDIDashItem;
import modelo.Usuario;

import java.io.IOException;
import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

public class DashboardGUIController implements javafx.fxml.Initializable {

    // Contêiner principal para os cards na UI
    @FXML private FlowPane cardsContainer;

    // A logoImage do topo (Pode ser mantida para evitar problemas, mas não é usada no FXML atual)
    @FXML private ImageView logoImage;

    // NOVO: ImageView para a logo na sidebar (fx:id="logoSidebar")
    @FXML private ImageView logoSidebar;

    // Botões e Focos
    @FXML private Button btnCadastroPdi; // AGORA DENTRO DA SIDEBAR
    @FXML private BorderPane rootPane;
    @FXML private TextField campoBusca;

    // Botão de Sair (Adicionado no passo anterior)
    @FXML private Button btnSair;

    // Sidebar e Containers de Checkbox
    @FXML private VBox checkboxContainerStatus;
    @FXML private VBox checkboxContainerArea;

    // Controles de Ordenação
    @FXML private ComboBox<String> comboOrdenacao;
    @FXML private Button btnInverterOrdem;
    @FXML private SVGPath iconSetaOrdem;

    // Listas para gerenciamento e filtragem de dados
    private ObservableList<PDIDashItem> listaObservable;
    private FilteredList<PDIDashItem> filteredData;
    private SortedList<PDIDashItem> sortedData;

    private boolean isAscending = false; // Estado inicial: Descendente (mais futuro primeiro)

    private Usuario usuarioLogado;
    // O DAO pode ser usado diretamente, mas o Helper é a camada de negócios
    private final DashboardDAO dao = new DashboardDAO();
    private final DashboardGUIControllerHelper helper = new DashboardGUIControllerHelper();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Carregar logo YouTan
        try {
            Image img = new Image(getClass().getResourceAsStream("/gui/images/logo_youtan_transparente.png"));

            // Carrega em ambas as referências (se existirem)
            if (logoImage != null) {
                logoImage.setImage(img);
            }
            if (logoSidebar != null) {
                logoSidebar.setImage(img);
            }
        } catch (Exception e) {
            System.err.println("Logo não encontrada: " + e.getMessage());
        }

        // Inicializa as listas
        listaObservable = FXCollections.observableArrayList();
        filteredData = new FilteredList<>(listaObservable, p -> true);
        sortedData = new SortedList<>(filteredData);

        // Configura os listeners de UI (Foco, Busca, Ordenação)
        setupFocusListener();
        setupSortingControls();
        setupFilteringListeners();
    }

    public void setUsuario(Usuario usuario) {
        this.usuarioLogado = usuario;

        // CORREÇÃO CRÍTICA: setVisible e setManaged controlam o espaço do botão na sidebar.
        if (btnCadastroPdi != null) {
            boolean isRh = "RH".equalsIgnoreCase(usuario.getTipoAcesso());
            btnCadastroPdi.setVisible(isRh);
            btnCadastroPdi.setManaged(isRh);
        }

        carregarDados();
    }


    private void setupFocusListener() {
        // Remove o foco ao clicar no painel principal
        rootPane.setOnMouseClicked(event -> {
            rootPane.requestFocus();
        });

        // Remove o foco do campo de busca ao sair
        campoBusca.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (oldVal && !newVal) {
                rootPane.requestFocus();
            }
        });
    }

    private void setupSortingControls() {
        comboOrdenacao.getItems().addAll("Prazo (Padrão)", "Objetivo", "Colaborador", "Status");
        comboOrdenacao.setValue("Prazo (Padrão)");

        comboOrdenacao.valueProperty().addListener((obs, oldVal, newVal) -> {
            aplicarOrdenacao();
        });

        // Define a rotação inicial do ícone para 0 (Descendente)
        if (iconSetaOrdem != null) {
            iconSetaOrdem.setRotate(0);
        }

        btnInverterOrdem.getStyleClass().add("descendente");

        aplicarOrdenacao();
    }

    @FXML
    private void inverterOrdem(ActionEvent event) {
        isAscending = !isAscending;

        if (iconSetaOrdem != null) {
            // Lógica para girar o ícone visualmente
            if (isAscending) {
                iconSetaOrdem.setRotate(180);
                btnInverterOrdem.getStyleClass().remove("descendente");
                btnInverterOrdem.getStyleClass().add("ascendente");
            } else {
                iconSetaOrdem.setRotate(0);
                btnInverterOrdem.getStyleClass().remove("ascendente");
                btnInverterOrdem.getStyleClass().add("descendente");
            }
        }

        aplicarOrdenacao();
    }


    private void setupFilteringListeners() {
        campoBusca.textProperty().addListener((observable, oldValue, newValue) -> {
            aplicarFiltros();
        });
    }

    private void setupCheckboxes(List<PDIDashItem> dados) {
        // --- 1. FILTRO DE STATUS ---
        checkboxContainerStatus.getChildren().clear();
        Set<String> statusUnicos = dados.stream()
                .map(PDIDashItem::getStatus)
                .distinct()
                .collect(Collectors.toSet());

        for (String status : statusUnicos) {
            CheckBox cb = new CheckBox(status);
            cb.selectedProperty().addListener((obs, oldVal, newVal) -> aplicarFiltros());
            checkboxContainerStatus.getChildren().add(cb);
        }

        // --- 2. FILTRO DE ÁREA ---
        checkboxContainerArea.getChildren().clear();
        Set<String> areasUnicas = dados.stream()
                .map(PDIDashItem::getArea)
                .distinct()
                .filter(area -> area != null && !area.isEmpty())
                .collect(Collectors.toSet());

        for (String area : areasUnicas) {
            CheckBox cb = new CheckBox(area);
            cb.selectedProperty().addListener((obs, oldVal, newVal) -> aplicarFiltros());
            checkboxContainerArea.getChildren().add(cb);
        }
    }

    /**
     * Busca os dados atualizados do banco/serviço e reinicia a ObservableList.
     * Utiliza o Helper para obter a lista correta baseado no acesso do usuário.
     */
    private void carregarDados() {
        if (usuarioLogado == null) return;

        // 1. BUSCA E ATUALIZA A LISTA BASE usando o Helper
        List<PDIDashItem> dados = helper.carregarDados(usuarioLogado);
        listaObservable.setAll(dados);

        // 2. RECRIA E CONFIGURA AS CHECKBOXES
        setupCheckboxes(dados);

        // 3. APLICA FILTROS E ORDENAÇÃO
        aplicarFiltros();
    }

    @FXML
    public void recarregarPdis(ActionEvent event) {
        // Limpa os filtros de UI (busca e checkboxes)
        campoBusca.setText("");

        checkboxContainerStatus.getChildren().stream()
                .filter(node -> node instanceof CheckBox)
                .map(node -> (CheckBox) node)
                .forEach(cb -> cb.setSelected(false));

        checkboxContainerArea.getChildren().stream()
                .filter(node -> node instanceof CheckBox)
                .map(node -> (CheckBox) node)
                .forEach(cb -> cb.setSelected(false));

        // Recarrega os dados do banco de dados/serviço
        carregarDados();
    }

    private void aplicarFiltros() {
        // 1. Coleta os valores selecionados nas Checkboxes
        Set<String> statusSelecionados = checkboxContainerStatus.getChildren().stream()
                .filter(node -> node instanceof CheckBox)
                .map(node -> (CheckBox) node)
                .filter(CheckBox::isSelected)
                .map(CheckBox::getText)
                .collect(Collectors.toSet());

        Set<String> areasSelecionadas = checkboxContainerArea.getChildren().stream()
                .filter(node -> node instanceof CheckBox)
                .map(node -> (CheckBox) node)
                .filter(CheckBox::isSelected)
                .map(CheckBox::getText)
                .collect(Collectors.toSet());

        // 2. Define o Predicate (regra de filtragem)
        filteredData.setPredicate(item -> {

            // A. FILTRO DE BUSCA (Texto Livre)
            String termoBusca = campoBusca.getText();
            if (termoBusca != null && !termoBusca.trim().isEmpty()) {
                String buscaLower = termoBusca.toLowerCase();

                boolean termoMatch = item.getObjetivo().toLowerCase().contains(buscaLower) ||
                        item.getColaborador().toLowerCase().contains(buscaLower) ||
                        (item.getArea() != null && item.getArea().toLowerCase().contains(buscaLower));

                if (!termoMatch) return false;
            }

            // B. FILTRO DE STATUS (Multi-seleção - Lógica OR)
            // Se nenhum status foi selecionado, não filtra por status.
            if (!statusSelecionados.isEmpty()) {
                if (!statusSelecionados.contains(item.getStatus())) return false;
            }

            // C. FILTRO DE ÁREA (Multi-seleção - Lógica OR)
            // Se nenhuma área foi selecionada, não filtra por área.
            if (!areasSelecionadas.isEmpty()) {
                if (item.getArea() == null || !areasSelecionadas.contains(item.getArea())) return false;
            }

            return true; // Passou em todos os filtros
        });

        // A SortedList é notificada automaticamente e precisamos garantir que a UI seja atualizada
        exibirCards(sortedData);
    }

    private void aplicarOrdenacao() {
        String criterio = comboOrdenacao.getValue();
        Comparator<PDIDashItem> comparator;

        switch (criterio) {
            case "Objetivo":
                comparator = Comparator.comparing(PDIDashItem::getObjetivo);
                break;
            case "Colaborador":
                comparator = Comparator.comparing(PDIDashItem::getColaborador);
                break;
            case "Status":
                comparator = Comparator.comparing(PDIDashItem::getStatus);
                break;
            case "Prazo (Padrão)":
            default:
                // Assume que getPrazo retorna uma string que pode ser comparada
                comparator = Comparator.comparing(PDIDashItem::getPrazo);
                break;
        }

        // Inverte o comparador se for Descendente
        if (!isAscending) {
            comparator = comparator.reversed();
        }

        // Aplica o comparador na SortedList
        sortedData.setComparator(comparator);

        // Atualiza a UI
        exibirCards(sortedData);
    }

    private void exibirCards(List<PDIDashItem> itens) {
        cardsContainer.getChildren().clear();

        for (PDIDashItem item : itens) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/PdiCard.fxml"));
                Parent cardNode = loader.load();
                PdiCardController cardController = loader.getController();
                cardController.setPdiItem(item);
                cardsContainer.getChildren().add(cardNode);

            } catch (IOException e) {
                System.err.println("Erro ao carregar PdiCard.fxml para o item: " + item.getObjetivo());
                e.printStackTrace();
            }
        }
    }

    // Este FXML Action agora se aplica ao botão dentro da Sidebar
    @FXML
    private void abrirCadastroPdi() {
        if (usuarioLogado != null && "RH".equalsIgnoreCase(usuarioLogado.getTipoAcesso())) {
            // Assumindo que CadastroPdiWindow e Usuario existem
            CadastroPdiWindow cadastro = new CadastroPdiWindow(usuarioLogado);
            cadastro.show();
        }
    }

    /**
     * Fecha o Dashboard e retorna para a tela de Login, garantindo o CSS.
     */
    @FXML
    private void handleLogout() {
        try {
            // 1. Fecha a janela atual (Dashboard)
            Stage dashStage = (Stage) btnSair.getScene().getWindow();
            dashStage.close();

            // 2. Carrega a tela de Login novamente (assumindo LoginGUI.fxml existe)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/LoginGUI.fxml"));
            Parent root = loader.load();

            // Reabre a Stage com a tela de Login
            Stage loginStage = new Stage();
            loginStage.setTitle("YouTan - Login");
            // Maximiza
            loginStage.setMaximized(true);
            Scene loginScene = new Scene(root);

            // *** CORREÇÃO CRÍTICA: REAPLICAÇÃO DO CSS ***
            // Garantimos que tanto o CSS global (style.css) quanto o específico (Login.css) sejam aplicados.
            loginScene.getStylesheets().add(getClass().getResource("/gui/css/style.css").toExternalForm());
            loginScene.getStylesheets().add(getClass().getResource("/gui/css/Login.css").toExternalForm());

            loginStage.setScene(loginScene);
            loginStage.show();

        } catch (IOException e) {
            System.err.println("Erro ao carregar a tela de Login: " + e.getMessage());
            e.printStackTrace();
        }
    }
}