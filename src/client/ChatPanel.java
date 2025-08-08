package client;

import java.awt.*;
import java.awt.event.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import javax.swing.*;
import model.Piece;

/**
 * Panel czatu gry.
 * Wyświetla informacje o graczach, oraz umożliwia wysyłanie wiadomości.
 * Automatycznie zapisuje czat do pliku tekstowego.
 * @author Grzegorz Dżyg
 */
public class ChatPanel extends JPanel {
    
    private static final String CHAT_HISTORY_FILE = "chat_history.txt"; 
    
    private GameClientSocket connection;

    private JTextArea chatArea;
    private JTextField inputField;
    private JButton sendButton;

    // Pola do informacji o graczach
    private JLabel nickPlayer1;
    private JLabel colorPlayer1;
    private JLabel statusPlayer1;

    private JLabel nickPlayer2;
    private JLabel colorPlayer2;
    private JLabel statusPlayer2;
    
    /**
     * Tworzy panel czatu z polami do wpisywania i wyświetlania wiadomości.
     */
    public ChatPanel() {
        setLayout(new BorderLayout());

        
        JPanel infoPanel = new JPanel(new GridLayout(3, 2));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        nickPlayer1 = new JLabel("Gracz: ???");
        nickPlayer1.setFont(new Font("SansSerif", Font.BOLD, 16));
        nickPlayer1.setForeground(Color.RED);

        colorPlayer1 = new JLabel("???");
        colorPlayer1.setFont(new Font("SansSerif", Font.BOLD, 16));
        colorPlayer1.setForeground(Color.RED);

        statusPlayer1 = new JLabel("Status: ???");
        statusPlayer1.setFont(new Font("SansSerif", Font.BOLD, 16));
        statusPlayer1.setForeground(Color.RED);

        nickPlayer2 = new JLabel("Gracz: ???");
        nickPlayer2.setFont(new Font("SansSerif", Font.BOLD, 16));
        nickPlayer2.setForeground(Color.BLUE);

        colorPlayer2 = new JLabel("???");
        colorPlayer2.setFont(new Font("SansSerif", Font.BOLD, 16));
        colorPlayer2.setForeground(Color.BLUE);

        statusPlayer2 = new JLabel("Status: ???");
        statusPlayer2.setFont(new Font("SansSerif", Font.BOLD, 16));
        statusPlayer2.setForeground(Color.BLUE);

        infoPanel.add(nickPlayer1);
        infoPanel.add(nickPlayer2);
        infoPanel.add(colorPlayer1);
        infoPanel.add(colorPlayer2);
        infoPanel.add(statusPlayer1);
        infoPanel.add(statusPlayer2);

        //Pole z wiadomościami
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setFocusable(false);
        chatArea.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(chatArea);

        //Pole do wpisywania wiadomości
        inputField = new JTextField();
        inputField.setPreferredSize(new Dimension(50, 40));
        sendButton = new JButton("Wyślij");
        sendButton.setPreferredSize(new Dimension(50, 40));

        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendChatMessage();
            }
        });
        inputField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendChatMessage();
            }
        });

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.SOUTH);

        add(infoPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);

        setPreferredSize(new Dimension(320, 0));
    }
    
    /**
     * Wysyła wiadomość czatu do serwera i dopisuje ją do historii.
     */
    private void sendChatMessage() {
        String message = inputField.getText().trim();
        if (!message.isEmpty() && connection != null) {
            connection.send("CHAT:" + message);
            inputField.setText("");
            appendMessageToFile(message);
        }
    }
    /**
     * Ustawia połączenie sieciowe klienta.
     * @param connection obiekt połączenia klienta z serwerem
     */
    public void setConnection(GameClientSocket connection) {
        this.connection = connection;
    }
    
    /**
     * Dopisuje wiadomość do pliku historii czatu.
     * @param msg wiadomość do zapisania w pliku
     */
    private void appendMessageToFile(String msg) {
    try (PrintWriter pw = new PrintWriter(new FileWriter(CHAT_HISTORY_FILE, true))) {
        pw.println(msg);
    } catch (IOException e) {
        e.printStackTrace();
    }
}
    /**
     * Dodaje wiadomość do okna czatu i zapisuje ją w pliku historii.
     * @param msg wiadomość do wyświetlenia i zapisania
     */
    public void appendMessage(String msg) {
        SwingUtilities.invokeLater(() -> {
            chatArea.append(msg + "\n");
            chatArea.setCaretPosition(chatArea.getDocument().getLength());
            appendMessageToFile(msg);
        });
    }
    /**
     * Ustawia informacje o graczach (nazwy, kolory, statusy).
     * @param myColor kolor bieżącego gracza
     */
    public void setPlayerInfo(Piece.Color myColor) {
    if (myColor == Piece.Color.WHITE) {
        nickPlayer1.setText("Gracz: Ty");
        colorPlayer1.setText("Kolor: BIAŁE");
        statusPlayer1.setText("Status: RUCH");

        nickPlayer2.setText("Gracz: Przeciwnik");
        colorPlayer2.setText("Kolor: CZARNE");
        statusPlayer2.setText("Status: CZEKAJ");
    } else {
        nickPlayer1.setText("Gracz: Przeciwnik");
        colorPlayer1.setText("Kolor: BIAŁE");
        statusPlayer1.setText("Status: RUCH");

        nickPlayer2.setText("Gracz: Ty");
        colorPlayer2.setText("Kolor: CZARNE");
        statusPlayer2.setText("Status: CZEKAJ");
    }
    }
    /**
     * Aktualizuje statusy graczy zależnie od bieżącej tury.
     * @param currentTurn kolor gracza wykonującego ruch
     * @param myColor kolor bieżącego gracza 
     */
    public void updateTurn(Piece.Color currentTurn, Piece.Color myColor) {
    boolean myTurn = (currentTurn == myColor);
    if (myColor == Piece.Color.WHITE) {
        statusPlayer1.setText("Status: " + (myTurn ? "RUCH" : "CZEKAJ"));
        statusPlayer2.setText("Status: " + (!myTurn ? "RUCH" : "CZEKAJ"));
    } else {
        statusPlayer2.setText("Status: " + (myTurn ? "RUCH" : "CZEKAJ"));
        statusPlayer1.setText("Status: " + (!myTurn ? "RUCH" : "CZEKAJ"));
    }
}

   
}