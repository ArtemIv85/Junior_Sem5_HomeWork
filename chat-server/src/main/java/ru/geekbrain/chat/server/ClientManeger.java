package ru.geekbrain.chat.server;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientManeger  implements Runnable{

    public final Socket socket;
    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader;
    private String name;

    public final static ArrayList<ClientManeger> clients = new ArrayList<>();


    public ClientManeger(Socket socket) {
        this.socket = socket;

        try {

            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            name = bufferedReader.readLine();
            clients.add(this);
            System.out.println(name + " подключился к чату.");
            broadcastMessage("Server: "+name+ " подключился к чату.");
        } catch (IOException e) {
            closeEverything(socket, bufferedReader,bufferedWriter);
        }
    }

    @Override
    public void run() {
        String messageFromClient;

        while (socket.isConnected()){
            try {
                messageFromClient = bufferedReader.readLine();
               /* if (messageFromClient == null){
                    //для macOS
                    closeEverything(socket,bufferedReader,bufferedWriter);
                    break;
                }*/
                broadcastMessage(messageFromClient);
            } catch (IOException e) {
                closeEverything(socket,bufferedReader,bufferedWriter);
                break;
            }
        }
    }


    private void broadcastMessage(String message) {
        String[] inMess;
        String[] outMess;
        System.out.println("Пришло соообщение: "+message);
        if (message.contains("@")){ //Если в сообщении есть разделитель @
            inMess = message.split(": ",2); //отделяем отправителя
            outMess = inMess[1].split("@",2); //разбиваем только на две части строку.
                                                        // Пользователь и сообщение
                                                        // повторное вхождение будет игнорировано
            //Отправка сообщения конкретному пользователю
            for (ClientManeger client : clients) {


                try {
                        if (client.name.equals(outMess[0])) { //если имя пользователя совпадает с пользователем в сообщении
                        client.bufferedWriter.write("Приватное сообщение от "+name+": "+outMess[1]);
                        client.bufferedWriter.newLine();
                        client.bufferedWriter.flush();
                    }

                } catch (IOException e) {
                    closeEverything(socket, bufferedReader, bufferedWriter);
                }

            }



        }
       else { //отправка сообщений всем пользователям

            for (ClientManeger client : clients) {


                try {
                    //отправка сообщений всем пользователям
                    if (!client.name.equals(name)) { //если имя слиента не совпадает с текущим
                        client.bufferedWriter.write(message);
                        client.bufferedWriter.newLine();
                        client.bufferedWriter.flush();
                    }

                } catch (IOException e) {
                    closeEverything(socket, bufferedReader, bufferedWriter);
                }

            }
        }

    }

    private void closeEverything(Socket socket, BufferedReader bufferedReader,BufferedWriter bufferedWriter){
        // удаление клиента из колекции
        removeClient();
        try {
            // закрываем буфер для чтения даннх
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            //Завершаем работу буфера для записи данных
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            //Закрытие соединения с клиентским сокетом
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void removeClient() {
        clients.remove(this);
        System.out.println(name + " покинул чат.");
        broadcastMessage("Srver: "+name+ " покинул чат.");
    }
}
