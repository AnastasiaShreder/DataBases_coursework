package Controllers;

import Models.Request;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RequestsController {
    private JournalController parent;
    private Connection connection;

    private List<Request> reqs_list;
    private ObservableList<String> goods_names;
    private ObservableList<Integer> num_reqs;


    @FXML
    private TableView<Request> table_reqs;
    @FXML
    private TableColumn<?, ?> num_req_col;
    @FXML
    private TableColumn<?, ?> good_name_col;
    @FXML
    private TableColumn<?, ?> good_count_col;
    @FXML
    private TableColumn<?, ?> create_date_col;

    @FXML
    private ComboBox<String> new_req_cb;
    @FXML
    private ComboBox<Integer> modify_req_cb;
    @FXML
    private ComboBox<Integer> delete_req_cb;

    @FXML
    private TextField new_req_cnt;
    @FXML
    private ComboBox<String> modify_req_good_cb;
    @FXML
    private TextField modify_req_cnt;

    @FXML
    private Button add_btn;
    @FXML
    private Button change_btn;
    @FXML
    private Button delete_btn;

    @FXML
    private DatePicker date_new_req;


    public void refreshReqs(){
        try {
            Statement stmt_1 = connection.createStatement();
            Statement stmt_2 = connection.createStatement();
            Statement stmt_3 = connection.createStatement();

            num_reqs = FXCollections.observableArrayList();
            reqs_list = new ArrayList<>();
            goods_names = FXCollections.observableArrayList();

            num_reqs.clear();
            reqs_list.clear();
            goods_names.clear();
            new_req_cnt.clear();
            date_new_req.setValue(null);

            ResultSet res_set1 = stmt_1.executeQuery("select SALES.ID, GOODS.name, SALES.GOOD_COUNT, SALES.create_date from SALES INNER JOIN GOODS ON SALES.GOOD_ID = GOODS.ID");
            ResultSet res_set2 = stmt_2.executeQuery("select GOODS.name from GOODS");
            ResultSet res_set3 = stmt_3.executeQuery("select SALES.ID from SALES");
            while (res_set1.next()) {
                reqs_list.add(new Request(res_set1.getInt(1), res_set1.getString(2), res_set1.getInt(3), res_set1.getDate(4)));
            }
            while (res_set2.next()) {
                goods_names.add(res_set2.getString(1));
            }
            while (res_set3.next()){
                num_reqs.add(res_set3.getInt(1));
            }

            new_req_cb.getItems().clear();
            new_req_cb.setItems(goods_names);

            modify_req_cb.getItems().clear();
            modify_req_cb.setItems(num_reqs);

            modify_req_good_cb.getItems().clear();
            modify_req_good_cb.setItems(goods_names);

            delete_req_cb.getItems().clear();
            delete_req_cb.setItems(num_reqs);

            table_reqs.getItems().clear();
            table_reqs.getItems().addAll(reqs_list);

        } catch (Exception e) {
            e.printStackTrace();
            parent.showErrorAlert(e.getMessage());
        }
    }
    public void provideReqs(JournalController parent, Connection connection){
        this.parent = parent;
        this.connection = connection;
        num_req_col.setCellValueFactory(new PropertyValueFactory<>("num"));
        good_name_col.setCellValueFactory(new PropertyValueFactory<>("name"));
        good_count_col.setCellValueFactory(new PropertyValueFactory<>("count"));
        create_date_col.setCellValueFactory(new PropertyValueFactory<>("date"));


        refreshReqs();
    }

    public void addReq() {
        try {
            try {
                if ((!(new_req_cnt.getText().equals(""))) & (!(new_req_cb.getSelectionModel().isEmpty()))) {
                    PreparedStatement stmt = connection.prepareStatement(
                            "DECLARE" +
                            " count_good NUMBER;" +
                            "gd_id NUMBER;" +
                            "my_count NUMBER;" +
                            "BEGIN " +
                            " my_count := ?;" +
                            " SELECT id INTO gd_id FROM GOODS WHERE GOODS.name = ?;" +
                            " SELECT good_count INTO count_good from warehouse1 WHERE good_id = gd_id;" +
                            " IF (count_good < my_count) THEN" +
                            "    UPDATE warehouse1 SET good_count = 0 WHERE good_id = gd_id;" +
                            "    UPDATE warehouse2 SET good_count = good_count - my_count + count_good WHERE good_id = gd_id;" +
                            "    INSERT INTO sales(GOOD_ID, GOOD_COUNT, CREATE_DATE) VALUES(gd_id, my_count, to_date(?, 'DD.MM.YYYY'));" +
                            " END IF;" +
                            "END;");
                    PreparedStatement stmt1 = connection.prepareStatement("DECLARE " +
                                                        "count_good NUMBER;"  +
                                                        "gd_id NUMBER;" +
                                                        "my_count NUMBER;" +
                                                        "BEGIN " +
                                                        "my_count := ?;" +
                                                        "SELECT id INTO gd_id FROM GOODS WHERE GOODS.name = ?;" +
                                                        "SELECT good_count INTO count_good from warehouse1 WHERE good_id = gd_id;" +
                                                        "IF (count_good >= my_count) THEN " +
                                                        "   UPDATE warehouse1 SET good_count = good_count - my_count WHERE good_id = gd_id;" +
                                                        "   INSERT INTO sales(GOOD_ID, GOOD_COUNT, CREATE_DATE) VALUES(gd_id, my_count, to_date(?, 'DD.MM.YYYY'));" +
                                                        "END IF;" +
                                                        "END;"
                    );
                    LocalDate curTime;
                    if (date_new_req.getValue() != null){
                        curTime = date_new_req.getValue();
                    } else {
                        curTime = LocalDate.now();
                    }

                    String ct = curTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                    stmt.setInt(1, Integer.parseInt(new_req_cnt.getText()));
                    stmt.setString(2, new_req_cb.getValue());
                    stmt.setString(3, ct);
                    stmt1.setInt(1, Integer.parseInt(new_req_cnt.getText()));
                    stmt1.setString(2, new_req_cb.getValue());
                    stmt1.setString(3, ct);
                    stmt.executeQuery();
                    stmt1.executeQuery();
                    refreshReqs();
                    parent.refreshJournal();
                    parent.showMessage("Заявка добавлена!");
                } else {
                    parent.showMessage("Недостаточно данных");
                }
            } catch (java.sql.SQLException e) {
                System.out.println(e.getMessage());
                throw new RuntimeException(e);
            }
        }catch (RuntimeException e) {
            System.out.println(e.getMessage());
            parent.showMessage(e.getMessage());
    }
    }
    public void modifyReq(){
        int flag = 0;
        try{
            if (!(modify_req_cb.getSelectionModel().isEmpty())) {
                if (!(modify_req_good_cb.getSelectionModel().isEmpty()))  {
                    PreparedStatement stmt = connection.prepareStatement("UPDATE sales SET sales.GOOD_ID = (SELECT id FROM GOODS WHERE GOODS.name = ?) WHERE Sales.ID = ?");
                    stmt.setString(1, modify_req_good_cb.getValue());
                    stmt.setInt(2, modify_req_cb.getValue());
                    stmt.executeQuery();
                    flag = 1;
                }
                if (!(modify_req_cnt.getText().equals(""))) {
                    PreparedStatement stmt = connection.prepareStatement("UPDATE sales SET sales.GOOD_COUNT = ? WHERE Sales.ID = ?");
                    stmt.setInt(1, Integer.parseInt(modify_req_cnt.getText()));
                    stmt.setInt(2, modify_req_cb.getValue());
                    stmt.executeQuery();
                    flag = 1;
                }
                if (flag == 1){
                    parent.showMessage("Заявка изменена!");
                } else {
                    parent.showMessage("Выберите новый товар или введите новое количество");
                }
                refreshReqs();
                parent.refreshJournal();
            } else {
                parent.showMessage("Выберите заявку");
            }
        } catch (Exception e) {
            e.printStackTrace();
            parent.showErrorAlert(e.getMessage());
        }
    }
    public void deleteReq(){
        try{
            if (delete_req_cb.getValue() != null){
                PreparedStatement stmt1 = connection.prepareStatement("DELETE FROM sales WHERE sales.id = ?");
                stmt1.setInt(1, delete_req_cb.getValue());
                stmt1.executeQuery();
                parent.showMessage("Заявка удалена!");
                refreshReqs();
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
