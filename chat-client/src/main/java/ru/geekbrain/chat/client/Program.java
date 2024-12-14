package ru.geekbrain.chat.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Program {
    public static void main(String[] args) {

        try {
            System.out.println("Start Client");
            Scanner scanner = new Scanner(System.in);
            System.out.print("Введите свое имя: ");
            String name = scanner.nextLine();

            Socket socket = new Socket("localhost", 1400);
            Client client = new Client(socket, name);

            //region Получение информации о соедениении
            InetAddress inetAddress = socket.getInetAddress();
            System.out.println("InetAddress: "+inetAddress);
            String remoteIP = inetAddress.getHostAddress();
            System.out.println("Remote IP: "+remoteIP); // IP адрес удаленной машины
            System.out.println("LocalPort: "+socket.getLocalPort()); // Адрес порта по которому общаеся с удаленной машиной
            System.out.println("Для приватных сообщений напишите ИМЯ с @ на конце без пробелов. Далее напишите само сообщение.");
            //endregion

            client.listenForMessage();
            client.senMessage();

        }
        catch (UnknownHostException e){
            e.fillInStackTrace();
        }
        catch (IOException e){
            e.printStackTrace();
        }

    }
}
