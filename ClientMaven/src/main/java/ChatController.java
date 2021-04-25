import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ChatController {
    private static boolean continueRead = true;
    private final String SERVER_IP = "localhost";
    private final int SERVER_PORT = 8189;

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    @FXML
    private TextArea chatArea;
    @FXML
    private TextField inputField;


    @FXML
    private void initialize() throws IOException {
        try {
            openConnection();
            addCloseListener();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка подключения");
            alert.setHeaderText("Сервер не работает");
            alert.showAndWait();
            e.printStackTrace();
            throw e;
        }
    }

    private void openConnection() throws IOException {
        socket = new Socket(SERVER_IP,SERVER_PORT);
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (continueRead) {
                        System.out.println("Начало чтения");
                        String strFromServer = in.readUTF();
                        System.out.println("Считал " + strFromServer);
                        if (strFromServer.equalsIgnoreCase("/end")) {
                            System.out.println("Конец цикла");
                            break;
                        }
                        chatArea.setText(chatArea.getText() + "Server: " + strFromServer + "\n");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void addCloseListener() {
        EventHandler<WindowEvent> onCloseRequest = Main.mainStage.getOnCloseRequest();
        Main.mainStage.setOnCloseRequest(event -> {
            closeConnection();
            if (onCloseRequest != null) {
                onCloseRequest.handle(event);
            }
        });
    }

    private void closeConnection() {
        try {
            continueRead = false;
            out.writeUTF("/end");
            socket.close();
            out.close();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void sendMsg() {
        if (!inputField.getText().trim().isEmpty()) {
            chatArea.setText(chatArea.getText() + "Я: " + inputField.getText().trim() + "\n");
            try {
                out.writeUTF(inputField.getText().trim());
                inputField.clear();
                inputField.requestFocus();
            } catch (IOException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Ошибка отправки сообщения");
                alert.setHeaderText("Ошибка отправки сообщения");
                alert.setContentText("При отправке сообщения возникла ошибка: " + e.getMessage());
                alert.show();
            }
        }
    }
}
