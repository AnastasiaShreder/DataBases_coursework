package Controllers;

import App.App;
import Models.User;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AuthController {
    private App parent;
    private Connection connection;
    private List<User> users;

    @FXML
    private TextField loginText;
    @FXML
    private PasswordField passwordField;


    public void provideApp(App parent, Connection connection) {
        this.parent = parent;
        this.connection = connection;
        users = new ArrayList<>();
        loadUsers();
    }

    @FXML
    public void enter() {
        final boolean[] found = {false};
        users.forEach(user -> {
            if (user.getLogin().equals(loginText.getText()) && user.getPassword().equals(passwordField.getText())) {
                parent.showJournalWindow();
                found[0] = true;
            }
        });
        if (!found[0]) {
            parent.showErrorAlert("Login error");
        }
    }

    private void loadUsers() {
        try {
            Statement stmt = connection.createStatement();

            ResultSet rs = stmt.executeQuery("select * from USERS");
            while (rs.next()) {
                users.add(new User(rs.getInt(1), rs.getString(2), rs.getString(3)));
            }
        } catch (Exception e) {
            e.printStackTrace();
            parent.showErrorAlert(e.getMessage());
        }
    }
}
