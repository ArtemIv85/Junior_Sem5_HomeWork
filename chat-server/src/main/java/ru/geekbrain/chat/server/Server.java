package ru.geekbrain.chat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private final ServerSocket serverSocket;


    public Server(ServerSocket serverSocket){
        this.serverSocket = serverSocket;
    }

    public void runServer(){
        try {
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept(); //Переводит основной поток в режим ожидания подлючения нового сокета
                ClientManeger clientManeger = new ClientManeger(socket);
                System.out.println("Подключился новый клиент!");

                Thread thread = new Thread(clientManeger); //создаем отдельный поток для нового clientManeger
                thread.start();
            }
        } catch (IOException e) {
            closeServerSocket();
        }

    }

    private void closeServerSocket(){
        try {
            if (serverSocket !=null) serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
