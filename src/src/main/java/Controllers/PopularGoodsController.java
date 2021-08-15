package Controllers;

import Models.Analytics;
import Models.Request;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;

import java.sql.Date;
import java.util.List;

public class PopularGoodsController {
    private JournalController parent;
    private Connection connection;
    private Stage stage;

    private ObservableList<Analytics> popGoods;

    @FXML
    private ListView<Analytics> popular_list;

    public void refreshAnalytics(String begin, String end){
        try{
//            Statement stmt = connection.createStatement();
            PreparedStatement stmt = connection.prepareStatement("SELECT goods.name, COUNT(sales.id) as requests_count " +
                    "FROM goods " +
                    "JOIN sales ON sales.good_id = goods.id " +
                    "WHERE (sales.create_date BETWEEN to_date(?, 'DD.MM.YYYY') AND to_date(?, 'DD.MM.YYYY')) " +
                    "GROUP BY goods.name, sales.good_id " +
                    "ORDER BY COUNT(sales.id) DESC " +
                    "FETCH FIRST 5 ROWS ONLY ");
            stmt.setString(1, begin);
            stmt.setString(2, end);
            ResultSet rs = stmt.executeQuery();
            popGoods.clear();

            while (rs.next()) {
                popGoods.add(new Analytics(rs.getString(1), rs.getInt(2)));
            }
            System.out.println(popGoods);
            popular_list.getItems().clear();
            popular_list.setItems(popGoods);

        } catch (Exception e) {
            e.printStackTrace();
            parent.showErrorAlert(e.getMessage());
        }
    }

    public void provideAnalytics(JournalController parent, Connection connection, String begin, String end){
        this.parent = parent;
        this.connection = connection;

        popGoods = FXCollections.observableArrayList();

        refreshAnalytics(begin,end);
    }
}
