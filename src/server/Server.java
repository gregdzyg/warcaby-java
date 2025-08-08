
package server;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Serwer gry warcaby.
 * Odpowiada za przyjmowanie połączeń od klientów oraz przekazywanie komunikatów między nimi.
 * @author Grzegorz Dżyg
 */
public class Server {
    
    private ServerSocket serverSocket;
    private ArrayList<ClientHandler> clients = new ArrayList<>();
    /**
     * Metoda uruchamiająca serwer gry.
     * @param args argumenty wiersza poleceń (nieużywane)
     */
    public static void main(String[] args) {
        new Server().startServer();
    }
    
    /**
     * Rozpoczyna nasłuchiwanie na połączenia od klientów.
     * Akceptuje dwóch graczy i rozpoczyna grę.
     */
    public void startServer(){
     
        try{
            
            serverSocket = new ServerSocket(8888);
            System.out.println("Serwer czeka na graczy...");
            
            while(clients.size() < 2){
                
                Socket clientSocket = serverSocket.accept();
                System.out.println("Nowy gracz: " + clientSocket.getInetAddress());
                
                ClientHandler client = new ClientHandler(clientSocket, this);
                clients.add(client);
                client.start();
                
                
            }
            clients.get(0).sendMessage("COLOR:WHITE");
            clients.get(1).sendMessage("COLOR:BLACK");
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
    /**
     * Przekazuje wiadomość od jednego klienta do wszystkich klientów.
     * @param message wiadomość do przekazania
     * @param sender klient, który wysłał wiadomość
     */
    public synchronized void relayMessage(String message, ClientHandler sender){
        for(ClientHandler client : clients){
            
                client.sendMessage(message);
            
        }
    }
    
    
   
}
