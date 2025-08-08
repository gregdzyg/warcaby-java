
package client;

import java.io.*;
import java.net.*;
import model.GameState;
import model.Piece;

/**
 * Wątek obsługujący połączenie klienta gry z serwerem.
 * Odpowiada za wysyłanie i odbiór wiadomości, synchronizację stanu gry oraz obsługę czatu.
 * @author Grzegorz Dżyg
 */
public class GameClientSocket extends Thread {
    
    private GameState gameState;
    private BoardPanel boardPanel;
    private ChatPanel chatPanel;
  
    private Piece.Color myColor;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    /**
     * Tworzy połączenie z serwerem gry.
     * @param host adres serwera
     * @param port port serwera
     * @param gameState obiekt stanu gry
     * @param boardPanel panel planszy
     * @param chatPanel panel czatu
     */
    public GameClientSocket(String host, int port, GameState gameState, BoardPanel boardPanel, ChatPanel chatPanel){
        this.chatPanel = chatPanel;
        this.gameState = gameState;
        this.boardPanel = boardPanel;
        
        
        try{
            socket = new Socket(host, port);
            in  = new BufferedReader( new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            System.out.println("Polaczona z serwerem");
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    /**
     * Wysyła wiadomość do serwera.
     * @param message wiadomość do wysłania
     */
    public void send(String message){
        out.println(message);
    }
     /**
     * Odbiera wiadomości od serwera i aktualizuje stan gry oraz czat.
     */
   @Override
    public void run() {
        try {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                
                if (line.startsWith("CHAT:")) {
                String msg = line.substring(5);
                if (chatPanel != null) {
                    chatPanel.appendMessage(msg);
                }
                continue;
            }
                
                // jeśli wiadomość to COLOR:...
                if (line.startsWith("COLOR:")) {
                    String color = line.split(":")[1].trim();
                    myColor = Piece.Color.valueOf(color);
                    boardPanel.setMyColor(myColor);
                    chatPanel.setPlayerInfo(myColor); 
                    
                    System.out.println("Twój kolor to: " + myColor);
                    continue; // ← przejdź do kolejnej linii
                }

                // dodaj linię do bufora
                sb.append(line).append("\n");

                // jeżeli dotarliśmy do TURN, kończymy zbieranie
                if (line.startsWith("TURN:")) {
                    System.out.println("Odebrano:\n" + sb.toString());
                    GameState newState = GameState.fromString(sb.toString());
                    gameState.copyFrom(newState);
                    chatPanel.updateTurn(gameState.getCurrentTurn(), myColor);
                    boardPanel.repaint();
                    sb.setLength(0); // czyść bufor
                }
                if (line.startsWith("GAME_OVER:")) {
                String winnerStr = line.substring("GAME_OVER:".length());
                String msg;
                if (winnerStr.equals("WHITE")) {
                    msg = "BIAŁE WYGRYWAJĄ!";
                } else if (winnerStr.equals("BLACK")) {
                 msg = "CZARNE WYGRYWAJĄ!";
                } else {
                    msg = "REMIS!";
                }
                boardPanel.showEndScreen(msg);
                
}
            }
        } catch (IOException e) {
            System.out.println("Rozłączono");
        }
    }
    
}
