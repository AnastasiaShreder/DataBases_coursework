package Controllers;

import App.App;
import Models.Analytics;
import Models.Request;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class AnalyticsController {
    private JournalController parent;
    private Connection connection;
    private Stage stage;

    private List<Analytics> analyt_list;
    private ObservableList<String> goods_names;
    private TreeMap<Date, Integer> data;

    @FXML
    private TableView<Analytics> analyt_table;
    @FXML
    private TableColumn<?, ?> demand_col;
    @FXML
    private TableColumn<?, ?> count_goods_col;
    @FXML
    private TableColumn<?, ?> date_col;

    @FXML
    private ComboBox<String> goods_cb;

    @FXML
    private DatePicker date_b;
    @FXML
    private DatePicker date_e;

    @FXML
    private Label good_name;


    @FXML
    private LineChart<String, Number> chart;
    @FXML
    private CategoryAxis axis_dates;

    public void refreshAnalytics(){
        try{
            goods_names = FXCollections.observableArrayList();
            analyt_list = new ArrayList<>();

            Statement stmt_1 = connection.createStatement();
            Statement stmt_2 = connection.createStatement();
            Statement stmt_3 = connection.createStatement();

            analyt_list.clear();
            goods_names.clear();
            date_b.setValue(null);
            date_e.setValue(null);

            ResultSet res_set2 = stmt_2.executeQuery("select name from GOODS");

            while (res_set2.next()) {
                goods_names.add(res_set2.getString(1));
            }

            goods_cb.getItems().clear();
            goods_cb.setItems(goods_names);

        } catch (Exception e) {
            e.printStackTrace();
            parent.showErrorAlert(e.getMessage());
        }
    }
    public void provideAnalytics(JournalController parent, Connection connection){
        this.parent = parent;
        this.connection = connection;
        demand_col.setCellValueFactory(new PropertyValueFactory<>("count_reqs"));
        count_goods_col.setCellValueFactory(new PropertyValueFactory<>("count"));
        date_col.setCellValueFactory(new PropertyValueFactory<>("date"));


        refreshAnalytics();
    }

    public void showPopularGoods()
    {
        try {
            if ((date_b.getValue() != null) & (date_e.getValue() != null)) {
                if ((date_b.getValue().compareTo(date_e.getValue()) < 0)) {
                    final FXMLLoader loader = new FXMLLoader(App.class.getResource("/popularGoods.fxml"));
                    stage = new Stage();
                    stage.setScene(new Scene(loader.load()));
                    stage.setTitle("Goods");
                    stage.show();
                    LocalDate begin = date_b.getValue();
                    LocalDate end = date_e.getValue();
                    String new_b = begin.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                    String new_e = end.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                    System.out.println(new_b);
                    PopularGoodsController popController = loader.getController();
                    popController.provideAnalytics(parent, connection, new_b, new_e);
                } else {
                    parent.showMessage("Дата выбрана некорректно.");
                }
            } else {
                parent.showMessage("Необходимо выбрать даты.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            parent.showErrorAlert(e.getMessage());
        }
    }


    public void showGoodAnalytics()
    {
        try{
            if ((date_b.getValue() != null) & (date_e.getValue() != null)){
                if ((date_b.getValue().compareTo(date_e.getValue()) < 0)){
                    if (goods_cb.getValue() != null){
                        analyt_list.clear();
                        String good = goods_cb.getValue();
                        good_name.setText(good);
                        LocalDate begin = date_b.getValue();
                        LocalDate end = date_e.getValue();
                        String b = begin.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                        String e = end.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                        PreparedStatement stmt = connection.prepareStatement("SELECT COUNT(goods.name) as demand, SUM(sales.GOOD_COUNT), sales.create_date FROM sales JOIN goods ON sales.good_id = goods.id" +
                                "    WHERE goods.name = ? AND (sales.create_date BETWEEN to_date(?, 'DD.MM.YYYY') AND to_date(?, 'DD.MM.YYYY')) GROUP BY sales.create_date, sales.good_id ORDER BY sales.create_date ");
                        stmt.setString(1, good);
                        stmt.setString(2, b);
                        stmt.setString(3, e);
                        ResultSet rs = stmt.executeQuery();
                        while (rs.next()) {
                            analyt_list.add(new Analytics(rs.getInt(1), rs.getInt(2), rs.getDate(3)));
                        }
                        analyt_table.getItems().clear();
                        analyt_table.getItems().addAll(analyt_list);
                        showDemand(analyt_list);
                        refreshAnalytics();
                    } else {
                        parent.showErrorAlert("Необходимо выбрать товар.");
                    }
                } else {
                    parent.showErrorAlert("Дата выбрана некорректно.");
                }
            } else {
                parent.showErrorAlert("Необходимо выбрать даты.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            parent.showErrorAlert(e.getMessage());
        }

    }

    public void showDemand(List<Analytics> list) {
        chart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();

        series.setName("Спрос");

        for (Analytics l: list){

                series.getData().add(new XYChart.Data<>(l.getDate().toString(), l.getCount_reqs()));
        }
        chart.getData().add(series);
    }

    public void getReport() {
        try (FileWriter writer = new FileWriter("report.txt", false)) {
            try {
                PreparedStatement stmt = connection.prepareStatement("SELECT * FROM" +
                        "    ((SELECT goods.id as id_, goods.name as name_, (warehouse2.good_count - warehouse1.good_count) AS count_goods, goods.priority as priority_ " +
                        "    FROM warehouse2 " +
                        "    INNER JOIN goods ON warehouse2.good_id = goods.id INNER JOIN warehouse1 ON warehouse2.good_id = warehouse1.good_id) " +
                        "    MINUS " +
                        "    (SELECT goods.id as id_, goods.name as name_, (warehouse2.good_count - warehouse1.good_count) AS count_goods, goods.priority as priority_ FROM warehouse2 " +
                        "    INNER JOIN goods ON warehouse2.good_id = goods.id INNER JOIN warehouse1 ON warehouse2.good_id = warehouse1.good_id " +
                        "    WHERE (warehouse2.good_count - warehouse1.good_count) = '0'))" +
                        "    ORDER BY priority_");
                ResultSet rs = stmt.executeQuery();

                int i = 1;
                String report = "\t\t Список для перевоза товаров \n";
                while (rs.next()){
                    Formatter formatter = new Formatter();
                    report = report + (formatter.format("%-1s %1s %25s %10s %15s %n", i + ")", rs.getString(2) , "(ID товара: " + rs.getInt( 1) + "\t", "Кол-во: " + rs.getInt(3)  + "\t", "Приоритет: " + rs.getInt(4)  + ")"));
                    i++;
                }
                writer.write(report );
                System.out.println(report);
            } catch (SQLException e){
                parent.showMessage(e.getMessage());
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
