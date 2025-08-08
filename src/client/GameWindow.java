
package client;

import java.awt.*;
import javax.swing.*;
import model.GameState;

/**
 * Okno gry warcaby (klient Swing).
 * @author Grzegorz Dżyg
 */
public class GameWindow extends JFrame{
     /**
     * Tworzy i wyświetla okno gry z planszą oraz czatem.
     */
    public GameWindow(){
        
        setTitle("Warcaby - Grzegorz Dżyg");
        setSize(1000, 700);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
        setVisible(true);
        
        
        add(new JPanel(), BorderLayout.NORTH);
        add(new JPanel(), BorderLayout.WEST);
        
        GameState state = new GameState();
        BoardPanel board = new BoardPanel(state);
        ChatPanel chat = new ChatPanel();
        GameClientSocket connection = new GameClientSocket("localhost", 8888, state, board, chat);
        board.setConnection(connection);
        chat.setConnection(connection);
        connection.start();
        add(board, BorderLayout.CENTER);
        add(chat, BorderLayout.EAST);
        
        
        
        revalidate();
        repaint();
    }
}
