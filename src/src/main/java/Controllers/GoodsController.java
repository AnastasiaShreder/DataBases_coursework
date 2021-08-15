package Controllers;

import App.App;
import Models.Good;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class GoodsController {
    private JournalController parent;
    private Connection connection;

    private List<Good> goods_list;
    private ObservableList<String> goods_names;
    private ObservableList<Integer> prts;
    private int cnt1;
    private int cnt2;

    @FXML
    private TableView<Good> table_goods;
    @FXML
    private TableColumn<?, ?> good_name_col;
    @FXML
    private TableColumn<?, ?> good_priority_col;
    @FXML
    private TableColumn<?, ?> good_count_col;

    @FXML
    private ComboBox<String> old_good_cb;
    @FXML
    private ComboBox<String> delete_good_cb;
    @FXML
    private ComboBox<Integer> priority_cb1; //на integer
    @FXML
    private ComboBox<Integer> priority_cb2; //на integer

    @FXML
    private TextField new_good;
    @FXML
    private TextField new_good_change;


    @FXML
    private Button add_btn;
    @FXML
    private Button change_btn;
    @FXML
    private Button delete_btn;

    public void refreshGoods(){
        try {
            prts = FXCollections.observableArrayList();
            goods_list = new ArrayList<>();
            goods_names = FXCollections.observableArrayList();

            Statement stmt_1 = connection.createStatement();
            Statement stmt_2 = connection.createStatement();

            prts.add(1);
            prts.add(2);
            prts.add(3);

            goods_list.clear();
            goods_names.clear();

            ResultSet res_set1 = stmt_1.executeQuery("select GOODS.name, GOODS.priority, WAREHOUSE1.GOOD_COUNT from GOODS FULL JOIN WAREHOUSE1 ON GOODS.ID = WAREHOUSE1.GOOD_ID");
            ResultSet res_set2 = stmt_2.executeQuery("select GOODS.name from GOODS");
            while (res_set1.next()) {
                goods_list.add(new Good(res_set1.getString(1), res_set1.getInt(2), res_set1.getInt(3)));
            }
            while (res_set2.next()) {
                goods_names.add(res_set2.getString(1));
            }
            new_good.clear();
            new_good_change.clear();

            priority_cb1.getItems().clear();
            priority_cb1.setItems(prts);

            priority_cb2.getItems().clear();
            priority_cb2.setItems(prts);

            table_goods.getItems().clear();
            table_goods.getItems().addAll(goods_list);

            old_good_cb.getItems().clear();
            old_good_cb.setItems(goods_names);

            delete_good_cb.getItems().clear();
            delete_good_cb.setItems(goods_names);

        } catch (Exception e) {
            e.printStackTrace();
            parent.showErrorAlert(e.getMessage());
        }
    }
    public void provideGoods(JournalController parent, Connection connection){
        this.parent = parent;
        this.connection = connection;
        good_name_col.setCellValueFactory(new PropertyValueFactory<>("name"));
        good_priority_col.setCellValueFactory(new PropertyValueFactory<>("priority"));
        good_count_col.setCellValueFactory(new PropertyValueFactory<>("count"));


//        goods_names = new ObservableList<String>();

        refreshGoods();
    }
    public void addGood(){
        try {
            if ((!(new_good.getText().equals(""))) & (!(priority_cb1.getSelectionModel().isEmpty()))){
                PreparedStatement stmt = connection.prepareStatement("BEGIN INSERT INTO goods(name, priority) VALUES(?, ?);" +
                        "INSERT INTO warehouse1(GOOD_ID, GOOD_COUNT) VALUES((SELECT id FROM GOODS WHERE GOODS.name = ?), 30);" +
                        "INSERT INTO warehouse2(GOOD_ID, GOOD_COUNT) VALUES((SELECT id FROM GOODS WHERE GOODS.name = ?), 30); END;");
                stmt.setString(1, new_good.getText());
                stmt.setInt(2, priority_cb1.getValue());
                stmt.setString(3, new_good.getText());
                stmt.setString(4, new_good.getText());
                stmt.executeQuery();
                refreshGoods();
                parent.refreshJournal();
                parent.showMessage("Товар добавлен!");
            } else {
                parent.showMessage("Недостаточно данных");
            }
        } catch (Exception e) {
              e.printStackTrace();
              parent.showErrorAlert(e.getMessage());
        }
    }
    public void modifyGood(){
        int flag = 0;
        try{
            if (!(old_good_cb.getSelectionModel().isEmpty())) {
                if (!(priority_cb2.getSelectionModel().isEmpty())) {
                    PreparedStatement stmt = connection.prepareStatement("UPDATE goods SET goods.priority = ? WHERE goods.name = ?");
                    stmt.setInt(1, priority_cb2.getValue());
                    stmt.setString(2, old_good_cb.getValue());
                    stmt.executeQuery();
                    flag = 1;
                }
                if (!(new_good_change.getText().equals(""))) {
                    PreparedStatement stmt = connection.prepareStatement("UPDATE goods SET goods.name = ? WHERE goods.name = ?");
                    stmt.setString(1, new_good_change.getText());
                    stmt.setString(2, old_good_cb.getValue());
                    stmt.executeQuery();
                    flag = 1;
                }
                if (flag == 1){
                    parent.showMessage("Товар изменен!");
                } else {
                    parent.showMessage("Введите новое наименование или выберите приоритет");
                }
                refreshGoods();
                parent.refreshJournal();
            } else {
                parent.showMessage("Выберите товар");
            }
        } catch (Exception e) {
            e.printStackTrace();
            parent.showErrorAlert(e.getMessage());
        }
    }
    public void deleteGood(){
        try{
            if (delete_good_cb.getValue() != null){
                PreparedStatement stmt1 = connection.prepareStatement("SELECT GOOD_COUNT FROM WAREHOUSE2 INNER JOIN GOODS on GOODS.ID = WAREHOUSE2.GOOD_ID WHERE goods.name = ?");
                PreparedStatement stmt2 = connection.prepareStatement("SELECT GOOD_COUNT FROM WAREHOUSE1 INNER JOIN GOODS on GOODS.ID = WAREHOUSE1.GOOD_ID WHERE goods.name = ?");
                PreparedStatement stmt3 = connection.prepareStatement("SELECT GOOD_ID FROM SALES INNER JOIN GOODS on GOODS.ID = SALES.GOOD_ID WHERE goods.name = ?");
                stmt1.setString(1, delete_good_cb.getValue());
                stmt2.setString(1, delete_good_cb.getValue());
                stmt3.setString(1, delete_good_cb.getValue());

                ResultSet rs1 = stmt1.executeQuery();
                ResultSet rs2 = stmt2.executeQuery();
                ResultSet rs3 = stmt3.executeQuery();

                while (rs1.next()) {
                    cnt1 = rs1.getInt(1);
                }
                while (rs2.next()) {
                    cnt2 = rs2.getInt(1);
                }
                //int cnt2 = rs2.getInt(2);
                if ((cnt1 > 0) | (cnt2 > 0) | rs3.next()){
                    parent.showMessage("Данный товар есть на складе или в продажах");
                    refreshGoods();
                    return;
                }
                PreparedStatement stmt = connection.prepareStatement("BEGIN " +
                        "DELETE FROM sales WHERE sales.good_id = (SELECT id FROM GOODS WHERE GOODS.name = ?);" +
                        "DELETE FROM warehouse1 WHERE WAREHOUSE1.good_id = (SELECT id FROM GOODS WHERE GOODS.name = ?);" +
                        "DELETE FROM warehouse2 WHERE WAREHOUSE2.good_id = (SELECT id FROM GOODS WHERE GOODS.name = ?);" +
                        "DELETE FROM goods WHERE GOODS.name = ?;" +
                        "END;");
                stmt.setString(1, delete_good_cb.getValue());
                stmt.setString(2, delete_good_cb.getValue());
                stmt.setString(3, delete_good_cb.getValue());
                stmt.setString(4, delete_good_cb.getValue());
                stmt.executeQuery();
                parent.showMessage("Товар удален!");
                refreshGoods();
                parent.refreshJournal();
            } else {
                parent.showMessage("Выберите товар");
            }
        } catch (Exception e) {
            e.printStackTrace();
            parent.showErrorAlert(e.getMessage());
        }
    }

}
