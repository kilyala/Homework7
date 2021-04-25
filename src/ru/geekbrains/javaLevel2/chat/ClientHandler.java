package ru.geekbrains.javaLevel2.chat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {
    private MyServer server;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private String name = "";

    public ClientHandler(MyServer server, Socket socket) {
        try {
            this.server = server;
            this.socket = socket;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());

            new Thread(() -> {
                try {
                    auth();
                    readMsg();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    closeConnection();
                }

            }).start();

        } catch (IOException e) {
            System.out.println("Проблемы при создании обработчика клиента");
            e.printStackTrace();
        }
    }

    private void auth() throws IOException {
        while (true) {
            String str = in.readUTF();
            if (str.startsWith("/auth")) {
                String [] parts = str.split(" ");
                String login = parts[1];
                String password = parts[2];
                String nick = server.getAuthService().getNickByLoginPass(login, password);
                if (nick != null) {
                    if (!server.inNickBusy(nick)) {
                        sendMsg("/authok " + nick);
                        name = nick;
                        server.broadcastMsg(name + " зашёл в чат");
                        server.subscribe(this);
                        return;

                    } else {
                        sendMsg("Учётная запись уже используется");
                    }

                } else {
                    sendMsg("Неверный логин/пароль");
                }
            } else {
                sendMsg("You need to authorize to start messaging. Use command </auth nick password>");
            }
        }
    }

    public void sendMsg(String msg) {
        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readMsg() throws IOException {
        while (true) {
            String strFromClient = in.readUTF();
            System.out.println("From " + name + ": " + strFromClient);
            if (strFromClient.startsWith("/")) {
                if (strFromClient.equals("/end")) {
                    return;
                }
                if (strFromClient.startsWith("/w ")) {
                    String[] parts = strFromClient.split(" ");
                    String targetNick = parts[1];
                    String msg = strFromClient.substring(3 + targetNick.length() + 1);
                    server.sendPrivateMsg(this, targetNick, msg);
                }
                continue;
            }
            server.broadcastMsg(name + ": " + strFromClient);
        }
    }

    public void closeConnection() {
        server.unsubscribe(this);
        server.broadcastMsg(name + " вышел из чата");
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }
}
