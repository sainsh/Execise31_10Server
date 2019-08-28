package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;

public class Main extends Application {

    private int clientNo = 0;

    TextArea ta = new TextArea();

    ArrayList<Socket> clients;


    @Override
    public void start(Stage primaryStage) throws Exception {
        Scene scene = new Scene(new ScrollPane(ta), 800,600);
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(scene);
        primaryStage.show();

        clients = new ArrayList<>();


        new Thread(() -> {

            try {
                ServerSocket serverSocket = new ServerSocket(1337);
                ta.appendText("Multithreaded Chat Server started at " + new Date() + "\n");


                while (true) {
                    Socket socket = serverSocket.accept();
                    clientNo++;

                    Platform.runLater(() -> {
                        ta.appendText("Starting thread for client " + clientNo + " at " + new Date() + "\n");

                        InetAddress inetAddress = socket.getInetAddress();
                        ta.appendText("Client " + clientNo + " 's host name is " + inetAddress.getHostName() + "\n");
                        ta.appendText("Client " + clientNo + "'s IP Address is " + inetAddress.getHostAddress() + "\n");
                        clients.add(socket);

                    });

                    new Thread(new HandleAClient(socket)).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


        }).start();
    }


    public static void main(String[] args) {
        launch(args);
    }

    private class HandleAClient implements Runnable {
        private Socket socket;
        String output = "";

        public HandleAClient(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                DataInputStream in = new DataInputStream(socket.getInputStream());

                while(true){

                    output = in.readUTF();

                    for (Socket socket: clients) {
                        new DataOutputStream(socket.getOutputStream()).writeUTF(output);
                    }


                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
