
package server;

import java.io.*;
import java.net.*;

/**
 * Obsługuje komunikację z pojedynczym klientem gry warcaby.
 * Realizuje odbiór oraz wysyłkę komunikatów do klienta.
 * @author Grzegorz Dżyg
 */
public class ClientHandler extends Thread {
    
    Server server;
    Socket socket = new Socket();
    BufferedReader in;
    PrintWriter out;
     /**
     * Tworzy nowy wątek do obsługi klienta.
     * @param socket gniazdo klienta
     * @param server serwer gry
     */
    public ClientHandler(Socket socket, Server server){
        
        this.socket = socket;
        this.server = server;
        
        try{
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
    /**
     * Odbiera wiadomości od klienta i przekazuje je do serwera.
     */
    @Override
    public void run(){
        
        try{
            String line;
            while((line = in.readLine()) != null){
                System.out.println("Odebrano od gracza: " + line);
                server.relayMessage(line, this);
            }
        }
        catch(IOException e ){
            System.out.println("Gracz rozłaczony.");
        }
        
    }
     /**
     * Wysyła wiadomość do klienta.
     * @param message wiadomość do wysłania
     */
    public void sendMessage(String message){
        System.out.println(message);
        out.println(message);
    }
    
    
    
}
