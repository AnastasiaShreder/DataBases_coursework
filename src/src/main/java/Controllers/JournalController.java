package Controllers;
import App.App;
import Models.Good;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class JournalController {
    private App parent;
    private Connection connection;
    private Stage stage;

    private List<Good> goods_wrh1;
    private List<Good> goods_wrh2;

    @FXML
    private TableView<Good> wrh1_table;
    @FXML
    private TableView<Good> wrh2_table;
    @FXML
    private TableColumn<?, ?> good_name_col_1;
    @FXML
    private TableColumn<?, ?> good_count_col_1;
    @FXML
    private TableColumn<?, ?> good_name_col_2;
    @FXML
    private TableColumn<?, ?> good_count_col_2;


    public void refreshJournal() {
        try {
            Statement stmt_1 = connection.createStatement();
            Statement stmt_2 = connection.createStatement();

            goods_wrh1.clear();
            goods_wrh2.clear();
            ResultSet res_set1 = stmt_1.executeQuery("select GOODS.name, WAREHOUSE1.GOOD_COUNT from GOODS INNER JOIN WAREHOUSE1 ON GOODS.ID = WAREHOUSE1.GOOD_ID");
            ResultSet res_set2 = stmt_2.executeQuery("select GOODS.name, WAREHOUSE2.GOOD_COUNT from GOODS INNER JOIN WAREHOUSE2 ON GOODS.ID = WAREHOUSE2.GOOD_ID");
            while (res_set1.next()) {
                goods_wrh1.add(new Good(res_set1.getString(1), res_set1.getInt(2)));
            }
            while (res_set2.next()) {
                goods_wrh2.add(new Good(res_set2.getString(1), res_set2.getInt(2)));
            }

            wrh1_table.getItems().clear();
            wrh1_table.getItems().addAll(goods_wrh1);

            wrh2_table.getItems().clear();
            wrh2_table.getItems().addAll(goods_wrh2);

        } catch (Exception e) {
            e.printStackTrace();
            parent.showErrorAlert(e.getMessage());
        }
    }

    public void provideApp(App parent, Connection connection) {
        this.parent = parent;
        this.connection = connection;
        good_name_col_1.setCellValueFactory(new PropertyValueFactory<>("name"));
        good_count_col_1.setCellValueFactory(new PropertyValueFactory<>("count"));
        good_name_col_2.setCellValueFactory(new PropertyValueFactory<>("name"));
        good_count_col_2.setCellValueFactory(new PropertyValueFactory<>("count"));
        goods_wrh1 = new ArrayList<>();
        goods_wrh2= new ArrayList<>();

        refreshJournal();
    }

    public void openGoodsWindow()
    {
        try {
            final FXMLLoader loader = new FXMLLoader(App.class.getResource("/goodsOperations.fxml"));
            stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Goods");
            stage.show();

            GoodsController goodsController = loader.getController();
            goodsController.provideGoods(this, connection);
        } catch (Exception e) {
            e.printStackTrace();
            parent.showErrorAlert(e.getMessage());
        }
    }
    public void openReqsWindow()
    {
        try {
            final FXMLLoader loader = new FXMLLoader(App.class.getResource("/requestsOperations.fxml"));
            stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Requests");
            stage.show();

            RequestsController reqsController = loader.getController();
            reqsController.provideReqs(this, connection);
        } catch (Exception e) {
            e.printStackTrace();
            parent.showErrorAlert(e.getMessage());
        }
    }

    public void openAnalyticsWindow()
    {
        try {
            final FXMLLoader loader = new FXMLLoader(App.class.getResource("/analytics.fxml"));
            stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Analytics");
            stage.show();

            AnalyticsController analyticsController = loader.getController();
            analyticsController.provideAnalytics(this, connection);
        } catch (Exception e) {
            e.printStackTrace();
            parent.showErrorAlert(e.getMessage());
        }
    }
    public void showErrorAlert(String message) {
        showErrorAlert(message);
    }
    public void showMessage (String message){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
