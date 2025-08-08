
package client;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import java.awt.event.*;
import javax.swing.*;
import model.GameState;
import model.Piece;



/**
 * Panel z planszą gry.
 * Odpowiada za rysowanie planszy, obsługę ruchów i interakcję z użytkownikiem.
 * @author Grzegorz Dżyg
 */
public class BoardPanel extends JPanel {
    
    private final int TILE_SIZE = 80;
    private final int BOARD_SIZE = 8;
    
    private int selectedRow = -1;
    private int selectedCol = -1;
    
    private Piece.Color myColor;
    
    boolean mustCaptureAgain = false;
    
    private GameState currentState;
    private GameClientSocket connection;
    /**
     * Tworzy panel planszy i ustawia stan gry.
     * @param state stan gry do wyświetlenia i modyfikacji
     */
    public BoardPanel(GameState state){
    this.connection = null;    
    this.currentState = state;
    //MouseAdapter do obsługi kliknięć myszką
    addMouseListener(new MouseAdapter() {
    @Override
    public void mousePressed(MouseEvent e) {
        int clickedCol = e.getX() / TILE_SIZE;
        int clickedRow = e.getY() / TILE_SIZE;
        if (mustCaptureAgain) {
            if (selectedRow == -1 || selectedCol == -1) return;

            if (currentState.makeMove(selectedRow, selectedCol, clickedRow, clickedCol)) {
                if (Math.abs(clickedRow - selectedRow) == 2 &&
                    currentState.captureAgain(clickedRow, clickedCol)) {
                    // kolejne bicie możliwe
                    selectedRow = clickedRow;
                    selectedCol = clickedCol;
                    mustCaptureAgain = true;
                } else {
                    // bicie zakończone
                    selectedRow = -1;
                    selectedCol = -1;
                    mustCaptureAgain = false;
                    currentState.switchTurn();
                    if (connection != null) {
                         connection.send(currentState.toString());
                    }
                }
            } else {
                //️nieprawidłowy ruch w trybie bicia — kończymy turę
                mustCaptureAgain = false;
                selectedRow = -1;
                selectedCol = -1;
                currentState.switchTurn();
                if (connection != null) {
                    connection.send(currentState.toString());
                }
            }

         repaint();
            return;
        }
        if (clickedRow >= 0 && clickedRow < 8 && clickedCol >= 0 && clickedCol < 8) {
            if (selectedRow == -1) {
                Piece piece = currentState.getPiece(clickedRow, clickedCol);
                if (piece != null && piece.getColor() == currentState.getCurrentTurn() &&
                    currentState.getCurrentTurn() == myColor) {
                    selectedRow = clickedRow;
                    selectedCol = clickedCol;
                } else {
                    selectedRow = -1;
                    selectedCol = -1;
                }
            } else {
                boolean mustCapture = currentState.hasCaptureMoves(currentState.getCurrentTurn());
                Piece piece = currentState.getPiece(selectedRow, selectedCol);
                boolean isCaptureAttempt = piece != null && (
                (piece.isQueen() && Math.abs(selectedRow - clickedRow) == Math.abs(selectedCol - clickedCol)) ||
                (!piece.isQueen() && Math.abs(selectedRow - clickedRow) == 2 && Math.abs(selectedCol - clickedCol) == 2));
                        if(mustCapture && !isCaptureAttempt) {
                            selectedRow = -1;
                            selectedCol = -1;
                            return;
                        }
                
                if (currentState.makeMove(selectedRow, selectedCol, clickedRow, clickedCol)) {
                    if (currentState.isGameOver()) {
                        Piece.Color winner = currentState.getWinner(); // nowa metoda, patrz niżej
                        String msg;
                        if (winner != null) {
                            msg = (winner == Piece.Color.WHITE ? "BIAŁE" : "CZARNE") + " WYGRYWAJĄ!";
                            if (connection != null) {
                                connection.send("GAME_OVER:" + (winner == Piece.Color.WHITE ? "WHITE" : "BLACK"));
                            }
                        } else {
                            msg = "REMIS!";
                            if (connection != null) {
                                connection.send("GAME_OVER:REMIS");
                            }
                        }
                        showEndScreen(msg);
                    return;
                    }

                    if(connection != null){
                        currentState.switchTurn();
                        connection.send(currentState.toString());
                    }
                    // Sprawdzenie, czy to było bicie
                    
                    boolean wasCapture = piece != null && (
                        (piece.isQueen() && Math.abs(clickedRow - selectedRow) == Math.abs(clickedCol - selectedCol)) ||
                        (!piece.isQueen() && Math.abs(clickedRow - selectedRow) == 2));
                        if (wasCapture) { 
                        boolean continuedCapture = piece != null && (
                        (piece.isQueen() && Math.abs(clickedRow - selectedRow) == Math.abs(clickedCol - selectedCol)) ||
                        (!piece.isQueen() && Math.abs(clickedRow - selectedRow) == 2));

                    if (continuedCapture && currentState.captureAgain(clickedRow, clickedCol)) {
                            // Kolejne bicie możliwe – zostaw zaznaczenie
                            selectedRow = clickedRow;
                            selectedCol = clickedCol;
                            mustCaptureAgain = true;
                        } else {
                            selectedRow = -1;
                            selectedCol = -1;
                            mustCaptureAgain = false;
                            currentState.switchTurn();    
                        }
                    } else {
                        // Zwykły ruch – resetuj zaznaczenie
                        selectedRow = -1;
                        selectedCol = -1;
                        currentState.switchTurn();
                    }
                } else {
                    selectedRow = -1;
                    selectedCol = -1;
                }
            }
        }

        repaint();
    }
});
        
    }
    //
    @Override
    //rysowanie planszy, pionków i zaznaczenia
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        
        for(int row = 0; row < BOARD_SIZE; row++){
            for(int col = 0; col < BOARD_SIZE; col++){
                boolean isLight = (row + col) % 2 == 0;
                g.setColor(isLight ? new Color(222, 184, 135) : new Color(102, 51, 0));
                g.fillRect(col*TILE_SIZE, row*TILE_SIZE, TILE_SIZE, TILE_SIZE);
                
                if(currentState.getPiece(row, col) != null){
                    Piece piece = currentState.getPiece(row, col);
                    if(piece.getColor() == Piece.Color.WHITE){
                        g.setColor(Color.WHITE);
                    }
                    else{
                        g.setColor(Color.BLACK);
                    }
                    int padding = 10; 
                    int x = col * TILE_SIZE + padding;
                    int y = row * TILE_SIZE + padding;
                    int size = TILE_SIZE - 2 * padding;
                    
                    g.fillOval(x, y, size, size);
                    
                    boolean isSelected = (row == selectedRow && col == selectedCol);
                    int extraSize = isSelected ? 6 : 0;
                    int offset = isSelected ? -3 : 0;

                    g.fillOval(x + offset, y + offset, size + extraSize, size + extraSize);
                    if (isSelected) {
                    
                    g.fillOval(x + offset + 3, y + offset + 3, size + extraSize, size + extraSize);
                    g.setColor(piece.getColor() == Piece.Color.WHITE ? Color.WHITE : Color.BLACK);
                    }
                    if (piece.isQueen()) {
                    g.setColor(Color.GREEN);
                    g.drawOval(x, y, size, size);
                    g.drawOval(x + 2, y + 2, size - 4, size - 4);
                    }
                }
            }
        }
        
      
    } 
    
     /**
     * Ustawia połączenie sieciowe klienta.
     * @param connection obiekt połączenia z serwerem
     */
    public void setConnection(GameClientSocket connection) {
    this.connection = connection;
    }
    
    /**
     * Ustawia stan gry.
     * @param gameState nowy stan gry
     */
    public void setGameState(GameState gameState) {
    this.currentState = gameState;
    }
    
    /**
     * Ustawia kolor gracza.
     * @param c kolor gracza (WHITE lub BLACK)
     */
    public void setMyColor(Piece.Color c){
        this.myColor = c;
    }
    /**
     * Wyświetla ekran końca gry z podanym komunikatem.
     * @param message komunikat do wyświetlenia na ekranie końcowym
     */
    public void showEndScreen(String message) {
    removeAll();
    repaint();

    JLabel resultLabel = new JLabel(message, SwingConstants.CENTER);
    resultLabel.setFont(new Font("SansSerif", Font.BOLD, 32));
    resultLabel.setForeground(Color.RED);

    JButton restartButton = new JButton("Zakończ");
    restartButton.setFont(new Font("SansSerif", Font.PLAIN, 24));
    restartButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            System.exit(0); // Zamyka aplikację
        }
    });

    setLayout(new BorderLayout());
    add(resultLabel, BorderLayout.CENTER);
    add(restartButton, BorderLayout.SOUTH);
    revalidate();
    repaint();
}
    
   
}

