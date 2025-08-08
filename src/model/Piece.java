
package model;

/**
 * Klasa reprezentująca pionek w grze w warcaby.
 * Obsługuje kolor pionka oraz status damki.
 * @author Grzegorz Dżyg
 */
public class Piece {
    /**
     * Typ wyliczeniowy kolorów pionka.
     */
    public enum Color {WHITE, BLACK}
    
    private Color color;
    private boolean isQueen;
    /**
     * Tworzy nowy pionek o zadanym kolorze.
     * @param color kolor pionka (WHITE lub BLACK)
     */
    public Piece(Color color){
        this.color = color;
        this.isQueen = false;
    }
    /**
     * Zwraca kolor pionka.
     * @return kolor pionka
     */
    public Color getColor(){
        return color;
    }
    /**
     * Sprawdza, czy pionek jest damką.
     * @return true jeśli pionek jest damką, false w przeciwnym razie
     */
    public boolean isQueen(){
        return isQueen;
    }
    /**
     * Ustawia pionek jako damkę.
     */
    public void makeQueen(){
        this.isQueen = true;
    }
    
}
