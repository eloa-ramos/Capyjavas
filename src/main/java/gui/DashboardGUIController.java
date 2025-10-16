package gui;

import dao.DashboardDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader; // NOVO: Para carregar o FXML do Card
import javafx.scene.Parent; // NOVO: Para representar o nó do Card
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane; // NOVO: Para o contêiner dos Cards
import modelo.PDIDashItem;
import modelo.Usuario;

import java.io.IOException; // Para tratamento de erro no carregamento do FXML
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class DashboardGUIController {

    // REMOVIDOS: TableView e TableColumns

    @FXML
    private FlowPane cardsContainer; // NOVO: Contêiner para os Cards

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
    // O DashboardDAO pode ser acessado pelo helper, mas vamos mantê-lo aqui por enquanto
    private final DashboardDAO dao = new DashboardDAO();
    private final DashboardGUIControllerHelper helper = new DashboardGUIControllerHelper(); // Usando o helper

    // O formatador dtFormatter não é mais necessário aqui, pois a formatação está no PDIDashItem

    public void setUsuario(Usuario usuario) {
        this.usuarioLogado = usuario;
        btnCadastroPdi.setVisible("RH".equalsIgnoreCase(usuario.getTipoAcesso()));

        carregarDados();
        configurarFiltros();
    }

    @FXML
    private void initialize() {
        // Toda a lógica de TableColumn, CellFactory, Tooltips e RowFactory FOI REMOVIDA
        // Pois agora o styling e tooltips serão tratados no PdiCard.fxml e PdiCardController.java

        // Carregar logo (MANTIDO)
        try {
            // Mantenho o caminho, mas agora com fundo transparente
            Image img = new Image(getClass().getResourceAsStream("/gui/images/logo_youtan_transparente.png"));
            logoImage.setImage(img);
        } catch (Exception e) {
            System.err.println("Logo não encontrada: " + e.getMessage());
        }
    }

    private void carregarDados() {
        if (usuarioLogado == null) return;

        // USA O HELPER PARA BUSCAR OS DADOS DE FORMA LIMPA
        List<PDIDashItem> dados = helper.carregarDados(usuarioLogado);

        listaOriginal = FXCollections.observableArrayList(dados);

        // NOVO: Exibe os cards iniciais
        exibirCards(listaOriginal);

        // Configuração dos ComboBoxes (MANTIDA)
        filtroStatus.getItems().setAll("Todos", "Concluído", "Em Andamento", "Atrasado");
        filtroStatus.setValue("Todos");

        filtroArea.getItems().clear();
        filtroArea.getItems().add("Todas");
        dados.stream()
                .map(PDIDashItem::getArea)
                .distinct()
                .sorted()
                .forEach(area -> {
                    if (area != null && !area.isEmpty()) filtroArea.getItems().add(area);
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
                .filter(item -> areaSelecionada.equals("Todas") || (item.getArea() != null && item.getArea().equals(areaSelecionada)))
                .collect(Collectors.toList());

        // NOVO: Exibe os cards filtrados
        exibirCards(filtrados);
    }

    /**
     * NOVO MÉTODO: Carrega o FXML do Card para cada PDIDashItem e o adiciona ao FlowPane.
     */
    private void exibirCards(List<PDIDashItem> itens) {
        cardsContainer.getChildren().clear(); // Limpa o FlowPane antes de adicionar novos Cards

        for (PDIDashItem item : itens) {
            try {
                // Carrega o FXML do Card
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/PdiCard.fxml"));
                Parent cardNode = loader.load();

                // Pega o Controller do Card
                PdiCardController cardController = loader.getController();

                // Popula o Card com os dados (o controller do Card cuida do styling e labels)
                cardController.setPdiItem(item);

                // Adiciona o Card ao FlowPane
                cardsContainer.getChildren().add(cardNode);

            } catch (IOException e) {
                // Em caso de erro ao carregar o FXML do Card
                System.err.println("Erro ao carregar PdiCard.fxml para o item: " + item.getObjetivo());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void abrirCadastroPdi() {
        if (usuarioLogado != null && "RH".equalsIgnoreCase(usuarioLogado.getTipoAcesso())) {
            // Assumo que CadastroPdiWindow é uma classe que você criou
            CadastroPdiWindow cadastro = new CadastroPdiWindow(usuarioLogado);
            cadastro.show();
        }
    }
}