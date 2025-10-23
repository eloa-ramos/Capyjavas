package gui;

import dao.DashboardDAO;
import dao.PDIDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene; // Make sure Scene is imported
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.stage.Modality; // Make sure Modality is imported
import javafx.stage.Stage;
import modelo.PDI;
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

    // Contêineres e Componentes Principais
    @FXML private BorderPane rootPane;
    @FXML private FlowPane cardsContainer;

    // --- ELEMENTOS DO MENU DE NAVEGAÇÃO SUPERIOR ---
    @FXML private Button btnHome;
    @FXML private Button btnPDIs;
    @FXML private Label lblBemVindo;

    // --- ELEMENTOS DA SIDEBAR ---
    @FXML private ImageView logoSidebar;
    @FXML private TextField campoBusca;
    @FXML private Button btnCadastroPdi; // Botão existente para Cadastrar PDI
    @FXML private Button btnSair; // Botão de Logout

    // --- NOVO BOTÃO ---
    @FXML private Button btnCadastroUsuario; // Botão para Cadastrar Usuário

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

    private boolean isAscending = false; // Estado inicial: Descendente

    private Usuario usuarioLogado;
    private final DashboardDAO dao = new DashboardDAO();
    private final DashboardGUIControllerHelper helper = new DashboardGUIControllerHelper();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Carregar logo YouTan
        try {
            Image img = new Image(getClass().getResourceAsStream("/gui/images/logo_youtan_transparente.png"));
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

        // Configura os listeners de UI
        setupFocusListener();
        setupSortingControls();
        setupFilteringListeners();
    }

    public void setUsuario(Usuario usuario) {
        this.usuarioLogado = usuario;

        if (usuario != null) {
            // Preenche o Label de Boas-Vindas
            if (lblBemVindo != null) {
                lblBemVindo.setText("Olá, " + usuario.getNome().split(" ")[0] + "!");
            }

            // Verifica se é RH para mostrar os botões corretos
            boolean isRh = "RH".equalsIgnoreCase(usuario.getTipoAcesso());

            // Visibilidade do botão de cadastrar PDI
            if (btnCadastroPdi != null) {
                btnCadastroPdi.setVisible(isRh);
                btnCadastroPdi.setManaged(isRh);
            }

            // --- CONTROLE DE VISIBILIDADE DO BOTÃO Cadastrar Usuário ---
            if (btnCadastroUsuario != null) {
                btnCadastroUsuario.setVisible(isRh); // Visível apenas para RH
                btnCadastroUsuario.setManaged(isRh); // Ocupa espaço apenas se visível
            }
            // --------------------------------------------------------
        }

        carregarDados(); // Carrega os dados após configurar o usuário
    }

    private void setupFocusListener() {
        rootPane.setOnMouseClicked(event -> rootPane.requestFocus());
        campoBusca.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (oldVal && !newVal) {
                rootPane.requestFocus();
            }
        });
    }

    private void setupSortingControls() {
        comboOrdenacao.getItems().addAll("Prazo (Padrão)", "Objetivo", "Colaborador", "Status");
        comboOrdenacao.setValue("Prazo (Padrão)");
        comboOrdenacao.valueProperty().addListener((obs, oldVal, newVal) -> aplicarOrdenacao());

        if (iconSetaOrdem != null) {
            iconSetaOrdem.setRotate(0); // Padrão descendente
        }
        btnInverterOrdem.getStyleClass().add("descendente");
        aplicarOrdenacao(); // Aplica ordenação inicial
    }

    @FXML
    private void inverterOrdem(ActionEvent event) {
        isAscending = !isAscending;
        if (iconSetaOrdem != null) {
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
        campoBusca.textProperty().addListener((observable, oldValue, newValue) -> aplicarFiltros());
    }

    private void setupCheckboxes(List<PDIDashItem> dados) {
        // --- Filtro de Status ---
        checkboxContainerStatus.getChildren().clear();
        Set<String> statusUnicos = dados.stream()
                .map(PDIDashItem::getStatus)
                .filter(s -> s != null && !s.isBlank()) // Evita status nulos ou vazios
                .distinct()
                .sorted() // Ordena alfabeticamente
                .collect(Collectors.toSet());

        for (String status : statusUnicos) {
            CheckBox cb = new CheckBox(status);
            cb.selectedProperty().addListener((obs, oldVal, newVal) -> aplicarFiltros());
            checkboxContainerStatus.getChildren().add(cb);
        }

        // --- Filtro de Área ---
        checkboxContainerArea.getChildren().clear();
        Set<String> areasUnicas = dados.stream()
                .map(PDIDashItem::getArea)
                .filter(area -> area != null && !area.isEmpty())
                .distinct()
                .sorted() // Ordena alfabeticamente
                .collect(Collectors.toSet());

        for (String area : areasUnicas) {
            CheckBox cb = new CheckBox(area);
            cb.selectedProperty().addListener((obs, oldVal, newVal) -> aplicarFiltros());
            checkboxContainerArea.getChildren().add(cb);
        }
    }

    private void carregarDados() {
        if (usuarioLogado == null) return;
        List<PDIDashItem> dados = helper.carregarDados(usuarioLogado);
        listaObservable.setAll(dados);
        setupCheckboxes(dados);
        aplicarFiltros(); // Aplica filtros e agora garante que os cards sejam exibidos
    }

    @FXML
    public void recarregarPdis(ActionEvent event) {
        campoBusca.setText("");
        clearCheckboxes(checkboxContainerStatus);
        clearCheckboxes(checkboxContainerArea);
        carregarDados(); // Recarrega do DAO e aplica filtros/ordenação
    }

    // Método auxiliar para limpar checkboxes
    private void clearCheckboxes(VBox container) {
        container.getChildren().stream()
                .filter(node -> node instanceof CheckBox)
                .map(node -> (CheckBox) node)
                .forEach(cb -> cb.setSelected(false));
    }


    private void aplicarFiltros() {
        Set<String> statusSelecionados = getSelectedCheckboxTexts(checkboxContainerStatus);
        Set<String> areasSelecionadas = getSelectedCheckboxTexts(checkboxContainerArea);
        String termoBusca = campoBusca.getText();

        filteredData.setPredicate(item -> {
            boolean buscaMatch = true;
            if (termoBusca != null && !termoBusca.trim().isEmpty()) {
                String buscaLower = termoBusca.toLowerCase();
                buscaMatch = item.getObjetivo().toLowerCase().contains(buscaLower) ||
                        item.getColaborador().toLowerCase().contains(buscaLower) ||
                        (item.getArea() != null && item.getArea().toLowerCase().contains(buscaLower));
            }

            boolean statusMatch = statusSelecionados.isEmpty() || statusSelecionados.contains(item.getStatus());
            boolean areaMatch = areasSelecionadas.isEmpty() || (item.getArea() != null && areasSelecionadas.contains(item.getArea()));

            return buscaMatch && statusMatch && areaMatch;
        });

        // CORREÇÃO FUNCIONAL: Força a atualização da interface gráfica
        exibirCards(sortedData);
    }

    // Método auxiliar para pegar textos dos checkboxes selecionados
    private Set<String> getSelectedCheckboxTexts(VBox container) {
        return container.getChildren().stream()
                .filter(node -> node instanceof CheckBox)
                .map(node -> (CheckBox) node)
                .filter(CheckBox::isSelected)
                .map(CheckBox::getText)
                .collect(Collectors.toSet());
    }

    private void aplicarOrdenacao() {
        String criterio = comboOrdenacao.getValue();
        Comparator<PDIDashItem> comparator;

        switch (criterio) {
            case "Objetivo":
                comparator = Comparator.comparing(PDIDashItem::getObjetivo, String.CASE_INSENSITIVE_ORDER);
                break;
            case "Colaborador":
                comparator = Comparator.comparing(PDIDashItem::getColaborador, String.CASE_INSENSITIVE_ORDER);
                break;
            case "Status":
                comparator = Comparator.comparing(PDIDashItem::getStatus, String.CASE_INSENSITIVE_ORDER);
                break;
            case "Prazo (Padrão)":
            default:
                // Tenta comparar como data, se falhar, compara como string
                comparator = Comparator.comparing(PDIDashItem::getPrazo, Comparator.nullsLast(Comparator.comparing(p -> {
                    try {
                        // Assume formato dd/MM/yyyy - ajuste se for diferente
                        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");
                        return java.time.LocalDate.parse(p, formatter);
                    } catch (Exception e) {
                        return java.time.LocalDate.MAX; // Coloca datas inválidas no final
                    }
                })));
                break;
        }

        if (!isAscending) {
            comparator = comparator.reversed();
        }

        sortedData.setComparator(comparator);
        // A interface gráfica (cardsContainer) deve ser atualizada para refletir a nova ordem.
        exibirCards(sortedData); // Força a reexibição dos cards na ordem correta
    }

    private void exibirCards(List<PDIDashItem> itens) {
        cardsContainer.getChildren().clear(); // Limpa os cards existentes

        if (itens == null) return; // Segurança extra

        for (PDIDashItem item : itens) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/PdiCard.fxml"));
                Parent cardNode = loader.load();
                PdiCardController cardController = loader.getController();
                // Passa o item, o usuário logado e a referência deste controller
                cardController.setupCard(item, usuarioLogado, this);
                cardsContainer.getChildren().add(cardNode); // Adiciona o novo card
            } catch (IOException e) {
                System.err.println("Erro ao carregar PdiCard.fxml para o item: " + item.getObjetivo());
                e.printStackTrace();
            } catch (Exception e) {
                System.err.println("Erro inesperado ao criar card para: " + item.getObjetivo());
                e.printStackTrace();
            }
        }
    }


    public void abrirJanelaDeEdicao(PDIDashItem item) {
        try {
            PDIDAO daoPdi = new PDIDAO(); // Usa PDIDAO
            PDI pdiCompleto = daoPdi.buscarPdiPorId(item.getIdPdi());

            if (pdiCompleto == null) {
                System.err.println("PDI com ID " + item.getIdPdi() + " não encontrado.");
                showAlert(Alert.AlertType.ERROR, "Erro", "Não foi possível encontrar os dados completos do PDI.");
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/EdicaoPdiGUI.fxml"));
            Parent root = loader.load();

            EdicaoPdiController controller = loader.getController();
            controller.carregarPdi(pdiCompleto, item, this);

            Stage stage = new Stage();
            stage.setTitle("Editar PDI - " + item.getColaborador());
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (IOException e) {
            System.err.println("Erro ao carregar EdicaoPdiGUI.fxml: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erro", "Não foi possível abrir a tela de edição.");
        } catch (Exception e) {
            System.err.println("Erro inesperado ao abrir edição: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erro", "Ocorreu um erro inesperado ao tentar editar o PDI.");
        }
    }

    public void atualizarDashboard() {
        carregarDados(); // Simplesmente recarrega os dados
    }

    // Ação do botão "Cadastrar Novo PDI"
    @FXML
    private void abrirCadastroPdi() {
        if (usuarioLogado != null && "RH".equalsIgnoreCase(usuarioLogado.getTipoAcesso())) {
            CadastroPdiWindow cadastro = new CadastroPdiWindow(usuarioLogado);
            cadastro.show(); // Este método cuida de criar e mostrar a janela
        } else {
            showAlert(Alert.AlertType.WARNING, "Acesso Negado", "Apenas usuários RH podem cadastrar PDIs.");
        }
    }

    // --- NOVO MÉTODO PARA ABRIR CADASTRO DE USUÁRIO ---
    @FXML
    private void abrirCadastroUsuario() {
        // Verifica se o usuário logado é RH antes de abrir
        if (usuarioLogado != null && "RH".equalsIgnoreCase(usuarioLogado.getTipoAcesso())) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/UsuarioGUI.fxml"));
                Parent root = loader.load();

                // Se UsuarioGUIController precisar de algo, configure aqui
                // UsuarioGUIController controller = loader.getController();
                // controller.setAlgo(...);

                Stage stage = new Stage();
                stage.setTitle("Cadastrar Novo Usuário");
                stage.setScene(new Scene(root));
                stage.initModality(Modality.APPLICATION_MODAL); // Bloqueia a janela do dashboard
                // Pode ser útil definir um tamanho mínimo/máximo se o FXML não for responsivo
                // stage.setMinWidth(600);
                // stage.setMinHeight(500);
                stage.showAndWait(); // Mostra e espera fechar

                // Opcional: Recarregar dados se o cadastro de usuário afetar algo no dashboard
                // carregarDados();

            } catch (IOException e) {
                System.err.println("Erro ao carregar UsuarioGUI.fxml: " + e.getMessage());
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Erro de Interface", "Não foi possível abrir a tela de cadastro de usuário.");
            }
        } else {
            // Caso não seja RH (segurança extra)
            showAlert(Alert.AlertType.WARNING, "Acesso Negado", "Apenas usuários RH podem cadastrar novos usuários.");
        }
    }
    // -----------------------------------------------------


    // --- MÉTODOS DE NAVEGAÇÃO SUPERIOR ---
    @FXML
    private void handleNavigateToHome() {
        try {
            Stage currentStage = (Stage) rootPane.getScene().getWindow();
            currentStage.close(); // Fecha o dashboard atual

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/HomeGUI.fxml"));
            Parent root = loader.load();
            HomeGUIController controller = loader.getController();
            controller.setUsuario(usuarioLogado);

            Stage homeStage = new Stage();
            homeStage.setTitle("YouTan - Home");
            homeStage.setScene(new Scene(root));
            homeStage.setMaximized(true); // Maximiza a nova tela
            homeStage.show();
        } catch (IOException e) {
            System.err.println("Erro ao carregar HomeGUI.fxml: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erro de Navegação", "Não foi possível carregar a tela Home.");
        }
    }

    @FXML
    private void handleNavigateToPDIs() {
        // Já está na tela correta, talvez recarregar?
        recarregarPdis(null);
    }

    @FXML
    private void handleLogout() {
        try {
            Stage currentStage = (Stage) btnSair.getScene().getWindow();
            currentStage.close(); // Fecha o dashboard

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/LoginGUI.fxml"));
            Parent root = loader.load();

            Stage loginStage = new Stage();
            loginStage.setTitle("YouTan - Login");
            loginStage.setMaximized(true);
            Scene loginScene = new Scene(root);

            // Reaplica CSSs da tela de login
            loginScene.getStylesheets().add(getClass().getResource("/gui/css/style.css").toExternalForm());
            loginScene.getStylesheets().add(getClass().getResource("/gui/css/Login.css").toExternalForm());

            loginStage.setScene(loginScene);
            loginStage.show();

        } catch (IOException e) {
            System.err.println("Erro ao carregar a tela de Login: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erro ao Sair", "Não foi possível retornar à tela de login.");
        }
    }

    // Método auxiliar para mostrar alertas
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null); // Sem cabeçalho
        alert.setContentText(message);
        alert.showAndWait();
    }
}