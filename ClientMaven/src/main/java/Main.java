import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    static Stage mainStage;
    @Override
    public void start(Stage primaryStage) throws Exception {
        mainStage = primaryStage;
        Parent root = FXMLLoader.load(ClassLoader.getSystemResource("chat.fxml"));
        primaryStage.setTitle("Чатик");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
    }
}
