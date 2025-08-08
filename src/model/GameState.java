
package model;
/**
 * Klasa reprezentująca stan gry w warcaby.
 * Przechowuje aktualne położenie pionków, kolej gracza oraz udostępnia metody obsługi logiki gry.
 * @author Grzegorz Dżyg
 */
public class GameState {
    
    private final int NUMBER_OF_ROWS = 8;
    private final int NUMBER_OF_COLUMNS = 8;
    /** Kolor gracza, który wykonuje aktualnie ruch.
     * Białe zaczynają*/
    private Piece.Color currentTurn = Piece.Color.WHITE;
    /** Dwuwymiarowa tablica przechowująca stan planszy. */
    private final Piece[][] gameState = new Piece[NUMBER_OF_ROWS][NUMBER_OF_COLUMNS];
    /**
     * Tworzy nowy, początkowy stan gry (rozstawienie pionków).
     */
    public GameState(){
        
        for(int row = 0; row < 3; row++){
            for(int col = 0; col < NUMBER_OF_COLUMNS; col++){
                if((row + col) % 2 != 0){
                    gameState[row][col] = new Piece(Piece.Color.WHITE);
                }
            }
        }
        for(int row = 5; row < 8; row++){
            for(int col = 0; col < NUMBER_OF_COLUMNS; col++){
                if((row + col) % 2 != 0){
                    gameState[row][col] = new Piece(Piece.Color.BLACK);
                }
            }
        }
    }
    /**
     * Zwraca kolor gracza wykonującego ruch.
     * @return aktualny kolor gracza
     */
    public Piece.Color getCurrentTurn(){
        return currentTurn;
    }
    /**
     * Przełącza ruch na drugiego gracza.
     */
    public void switchTurn(){
        currentTurn = (currentTurn == Piece.Color.WHITE) ? Piece.Color.BLACK : Piece.Color.WHITE;
    }
    /**
     * Zwraca pionek na danym polu planszy.
     * @param row numer wiersza (0-7)
     * @param col numer kolumny (0-7)
     * @return pionek lub null jeśli pole jest puste
     */
    public Piece getPiece(int row, int col){
        return gameState[row][col];
    }
    /**
     * Sprawdza, czy dany pionek może wykonać kolejne bicie.
     * @param row wiersz pionka
     * @param col kolumna pionka
     * @return true jeśli możliwe jest kolejne bicie, false w przeciwnym razie
     */
    public boolean captureAgain(int row, int col){
        
        Piece selected = getPiece(row, col);
        if(selected == null) return false;
        
        int[][] directions = {
            {-2, 2},
            {-2, -2},
            {2, -2},
            {2, 2}
        };
        
        if (selected.isQueen()) {
            for (int stepRow = -1; stepRow <= 1; stepRow += 2) {
                for (int stepCol = -1; stepCol <= 1; stepCol += 2) {
                    int r = row + stepRow;
                    int c = col + stepCol;
                    boolean enemyFound = false;

                    while (r >= 0 && r < 8 && c >= 0 && c < 8) {
                        Piece p = getPiece(r, c);
                
                        if (p == null) {
                            if (enemyFound) {
                                // przeciwnik był, a to wolne pole – można tu wylądować po biciu
                                return true;
                            }
                        } else if (p.getColor() != selected.getColor()) {
                            if (enemyFound) break; // już był jeden wróg – nie można dwóch
                            enemyFound = true;
                        } else {
                            break; // własny pionek – blokuje
                        }

                        r += stepRow;
                        c += stepCol;
                    }
                }
            }
        }
        
        for(int[] dir : directions){
            
            int toRow = row + dir[0];
            int toCol = col + dir[1];
            
            if(toRow >= 0 && toRow < 8 && toCol >= 0 && toCol < 8){
                int midRow = (row + toRow) / 2;
                int midCol = (col + toCol) / 2;
                
                Piece middle = getPiece(midRow, midCol);
                Piece destination = getPiece(toRow, toCol);
                
                if(middle != null && middle.getColor() != selected.getColor() && destination == null){
                    return true;
                }
            }
        }
        return false;
    }
    /**
     * Sprawdza, czy gracz o podanym kolorze ma dostępne bicie.
     * @param playerColor kolor gracza
     * @return true jeśli istnieje możliwe bicie, false w przeciwnym razie
     */
    public boolean hasCaptureMoves(Piece.Color playerColor) {
    int[][] directions = {
        {-2, 2},
        {-2, -2},
        {2, -2},
        {2, 2}
    };

    for (int i = 0; i < 8; i++) {
        for (int j = 0; j < 8; j++) {
            Piece piece = getPiece(i, j);
            if (piece == null || piece.getColor() != playerColor) continue;

            if (!piece.isQueen()) {
                for (int[] dir : directions) {
                    int toRow = i + dir[0];
                    int toCol = j + dir[1];
                    int midRow = (i + toRow) / 2;
                    int midCol = (j + toCol) / 2;

                    if (toRow >= 0 && toRow < 8 && toCol >= 0 && toCol < 8 &&
                        midRow >= 0 && midRow < 8 && midCol >= 0 && midCol < 8) {
                        Piece destination = getPiece(toRow, toCol);
                        Piece middle = getPiece(midRow, midCol);
                        if (destination == null && middle != null && middle.getColor() != playerColor) {
                            return true;
                        }
                    }
                }
            }

            
            if (piece.isQueen()) {
                for (int stepRow = -1; stepRow <= 1; stepRow += 2) {
                    for (int stepCol = -1; stepCol <= 1; stepCol += 2) {
                        int r = i + stepRow;
                        int c = j + stepCol;
                        boolean enemyFound = false;

                        while (r >= 0 && r < 8 && c >= 0 && c < 8) {
                            Piece p = getPiece(r, c);
                            if (p == null) {
                                if (enemyFound) return true; // po przeciwniku puste pole
                            } else if (p.getColor() != playerColor) {
                                if (enemyFound) break; // już jeden wróg był – nie można więcej
                                enemyFound = true;
                            } else {
                                break; // własny pionek – koniec
                            }
                            r += stepRow;
                            c += stepCol;
                        }
                    }
                }
            }
        }
    }

    return false;
}
    
   /**
     * Obsługuje wykonanie ruchu na planszy.
     * @param fromRow początkowy wiersz
     * @param fromCol początkowa kolumna
     * @param toRow docelowy wiersz
     * @param toCol docelowa kolumna
     * @return true jeśli ruch był poprawny, false w przeciwnym razie
     */
   public boolean makeMove(int fromRow, int fromCol, int toRow, int toCol) {
    Piece selectedPiece = gameState[fromRow][fromCol];
    if (selectedPiece == null || selectedPiece.getColor() != getCurrentTurn()) return false;

    boolean moveIsCapture = false;

    
    if (selectedPiece.isQueen()) {
        int rowDiff = toRow - fromRow;
        int colDiff = toCol - fromCol;

        if (Math.abs(rowDiff) == Math.abs(colDiff)) {
            int stepRow = rowDiff > 0 ? 1 : -1;
            int stepCol = colDiff > 0 ? 1 : -1;
            int r = fromRow + stepRow;
            int c = fromCol + stepCol;
            boolean enemyFound = false;

            while (r != toRow && c != toCol) {
                Piece p = gameState[r][c];
                if (p != null) {
                    if (p.getColor() == selectedPiece.getColor()) break;
                    if (enemyFound) break;
                    enemyFound = true;
                }
                r += stepRow;
                c += stepCol;
            }

            if (enemyFound && gameState[toRow][toCol] == null) {
                moveIsCapture = true;
            }
        }
    } else if (Math.abs(toRow - fromRow) == 2 && Math.abs(toCol - fromCol) == 2) {
        int midRow = (fromRow + toRow) / 2;
        int midCol = (fromCol + toCol) / 2;
        Piece middle = getPiece(midRow, midCol);
        if (middle != null && middle.getColor() != selectedPiece.getColor()) {
            moveIsCapture = true;
        }
    }

    //Jeśli bicie jest dostępne, a ten ruch to nie bicie — zabroń
    if (hasCaptureMoves(getCurrentTurn()) && !moveIsCapture) {
        return false;
    }

    //Obsługa ruchu damki
    if (selectedPiece.isQueen()) {
        int rowDiff = toRow - fromRow;
        int colDiff = toCol - fromCol;

        if (Math.abs(rowDiff) == Math.abs(colDiff)) {
            int stepRow = rowDiff > 0 ? 1 : -1;
            int stepCol = colDiff > 0 ? 1 : -1;
            int r = fromRow + stepRow;
            int c = fromCol + stepCol;

            boolean enemyFound = false;
            int enemyRow = -1;
            int enemyCol = -1;

            while (r != toRow && c != toCol) {
                Piece p = gameState[r][c];
                if (p != null) {
                    if (p.getColor() == getCurrentTurn()) return false;
                    if (enemyFound) return false;
                    enemyFound = true;
                    enemyRow = r;
                    enemyCol = c;
                }
                r += stepRow;
                c += stepCol;
            }

            if (gameState[toRow][toCol] == null) {
                gameState[toRow][toCol] = selectedPiece;
                gameState[fromRow][fromCol] = null;
                if (enemyFound) {
                    gameState[enemyRow][enemyCol] = null; // zbicie
                }
                return true;
            }
        }

        return false;
    }

    //Zwykły ruch pionka
    if (!selectedPiece.isQueen()) {
        int dir = (selectedPiece.getColor() == Piece.Color.WHITE) ? 1 : -1;

        // zwykły ruch
        if (toRow == fromRow + dir && Math.abs(toCol - fromCol) == 1 && gameState[toRow][toCol] == null) {
            gameState[toRow][toCol] = selectedPiece;
            gameState[fromRow][fromCol] = null;
            if ((toRow == 0 && selectedPiece.getColor() == Piece.Color.BLACK) ||
                (toRow == 7 && selectedPiece.getColor() == Piece.Color.WHITE)) {
                selectedPiece.makeQueen();
            }
            return true;
        }

        // bicie
        if (Math.abs(toRow - fromRow) == 2 && Math.abs(toCol - fromCol) == 2) {
            int midRow = (fromRow + toRow) / 2;
            int midCol = (fromCol + toCol) / 2;
            Piece middle = getPiece(midRow, midCol);
            if (middle != null && middle.getColor() != getCurrentTurn() && gameState[toRow][toCol] == null) {
                gameState[toRow][toCol] = selectedPiece;
                gameState[fromRow][fromCol] = null;
                gameState[midRow][midCol] = null;
                if ((toRow == 0 && selectedPiece.getColor() == Piece.Color.BLACK) ||
                    (toRow == 7 && selectedPiece.getColor() == Piece.Color.WHITE)) {
                    selectedPiece.makeQueen();
                }
                return true;
            }
        }
    }

    return false;
}
    /**
     * Sprawdza, czy gracz ma jakikolwiek możliwy ruch.
     * @param color kolor gracza
     * @return true jeśli istnieje możliwy ruch, false w przeciwnym razie
     */
    public boolean hasAnyValidMoves(Piece.Color color) {
    for (int row = 0; row < 8; row++) {
        for (int col = 0; col < 8; col++) {
            Piece piece = getPiece(row, col);
            if (piece != null && piece.getColor() == color) {
                //Jeśli ma bicie — ma ruch
                if (captureAgain(row, col)) {
                    return true;
                }

                //Zwykły pionek – sprawdzamy tylko możliwe kroki
                if (!piece.isQueen()) {
                    int dir = (color == Piece.Color.WHITE) ? 1 : -1;
                    for (int dcol = -1; dcol <= 1; dcol += 2) {
                        int newRow = row + dir;
                        int newCol = col + dcol;
                        if (newRow >= 0 && newRow < 8 && newCol >= 0 && newCol < 8) {
                            if (getPiece(newRow, newCol) == null) {
                                return true;
                            }
                        }
                    }
                }

                //Damka – sprawdzamy ruchy w każdą stronę
                if (piece.isQueen()) {
                    for (int drow = -1; drow <= 1; drow += 2) {
                        for (int dcol = -1; dcol <= 1; dcol += 2) {
                            int r = row + drow;
                            int c = col + dcol;
                            while (r >= 0 && r < 8 && c >= 0 && c < 8) {
                                if (getPiece(r, c) == null) return true;
                                else break;

                            }
                        }
                    }
                }
            }
        }
    }
    return false;
    }
    /**
     * Sprawdza, czy gra się zakończyła (brak możliwych ruchów).
     * @return true jeśli gra zakończona, false w przeciwnym razie
     */
    public boolean isGameOver() {
    boolean whiteHasPieces = false;
    boolean blackHasPieces = false;

    for (int i = 0; i < 8; i++) {
        for (int j = 0; j < 8; j++) {
            Piece piece = getPiece(i, j);
            if (piece != null) {
                if (piece.getColor() == Piece.Color.WHITE) whiteHasPieces = true;
                if (piece.getColor() == Piece.Color.BLACK) blackHasPieces = true;
            }
        }
    }

    if (!whiteHasPieces || !hasAnyValidMoves(Piece.Color.WHITE)) return true;
    if (!blackHasPieces || !hasAnyValidMoves(Piece.Color.BLACK)) return true;

    return false;
    }
    /**
     * Zwraca tekstową reprezentację stanu gry.
     * @return stan gry jako tekst
     */
    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        for(int row = 0; row < 8; row++){
            for(int col = 0; col < 8; col++){
                Piece p = getPiece(row, col);
                if(p == null){
                    sb.append(".");
                }
                else{
                    String symbol = p.getColor() == Piece.Color.WHITE ? "w" : "b";
                    if(p.isQueen()) symbol = symbol.toUpperCase();
                    sb.append(symbol);
                }
            }
            sb.append("\n");
        }
        sb.append("TURN:").append(currentTurn);
        return sb.toString();
    }
    /**
     * Tworzy nowy obiekt GameState na podstawie tekstowej reprezentacji.
     * @param data tekstowa reprezentacja stanu gry
     * @return nowy obiekt GameState
     */
    public static GameState fromString(String data){
        GameState state = new GameState();
        String[] lines = data.split("\n");
        for(int row = 0; row < 8; row++){
            for (int col = 0; col < 8; col++){
                char ch = lines[row].charAt(col);
                if(ch == '.'){
                    state.gameState[row][col] = null;
                }
                else{
                    Piece.Color color = Character.toLowerCase(ch) == 'w' ? Piece.Color.WHITE : Piece.Color.BLACK;
                    boolean isQueen = Character.isUpperCase(ch);
                    state.gameState[row][col] = new Piece(color);
                    if(isQueen){
                        state.gameState[row][col].makeQueen();
                    }
                }
            }
        }
        state.currentTurn = Piece.Color.valueOf(lines[8].split(":")[1].trim());
        return state;
    }
    
    /**
     * Kopiuje stan gry z innego obiektu GameState.
     * @param other stan gry do skopiowania
     */
    public void copyFrom(GameState other) {
    for (int row = 0; row < 8; row++) {
        for (int col = 0; col < 8; col++) {
            Piece otherPiece = other.getPiece(row, col);
            if (otherPiece == null) {
                this.gameState[row][col] = null;
            } else {
                Piece copied = new Piece(otherPiece.getColor());
                if (otherPiece.isQueen()) {
                    copied.makeQueen();
                }
                this.gameState[row][col] = copied;
            }
        }
    }
    this.currentTurn = other.getCurrentTurn();
}
    /**
     * Zwraca kolor zwycięzcy lub null w przypadku remisu.
     * @return kolor wygranego gracza lub null jeśli remis
     */
    public Piece.Color getWinner() {
    boolean whiteHasMoves = hasAnyValidMoves(Piece.Color.WHITE);
    boolean blackHasMoves = hasAnyValidMoves(Piece.Color.BLACK);

    if (!whiteHasMoves && blackHasMoves) return Piece.Color.BLACK;
    if (!blackHasMoves && whiteHasMoves) return Piece.Color.WHITE;

    return null; 
}
    
}       
       
            
        
        
    
    

