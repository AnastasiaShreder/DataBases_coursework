package App;

import Controllers.AuthController;
import Controllers.JournalController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;

public class App extends Application {
    private Stage primaryStage;
    private Connection connection;

    @Override
    public void start(Stage primaryStage) throws IOException {
        this.primaryStage = primaryStage;
        try {
            Class.forName("oracle.jdbc.OracleDriver");
            //DriverManager.registerDriver(new OracleDriver());

            connection = DriverManager.getConnection(
                    "jdbc:oracle:thin:@localhost:1521:xe", "c##test", "MyPass");
            showAuthWindow();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showAuthWindow() {
        try {
            final FXMLLoader loader = new FXMLLoader(getClass().getResource("/authentication.fxml"));
            primaryStage.setScene(new Scene(loader.load()));
            primaryStage.setTitle("Авторизация");
            primaryStage.show();

            AuthController authController = loader.getController();
            authController.provideApp(this, connection);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showJournalWindow() {
        try {
            final FXMLLoader loader = new FXMLLoader(getClass().getResource("/journal.fxml"));
            primaryStage.setScene(new Scene(loader.load()));
            primaryStage.setTitle("Журнал");
            primaryStage.show();

            JournalController jController = loader.getController();
            jController.provideApp(this, connection);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void showErrorAlert(String message) {
        final Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initOwner(primaryStage);
        alert.setTitle("Ошибка");
        alert.setHeaderText("Подробная информация об ошибке: ");
        alert.setContentText(message);

        alert.showAndWait();
    }
}