package sample;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * @author Ilya Klimov
 */

public class Tile extends Rectangle {
    private Piece piece;
    public boolean hasPiece() {
        return piece != null;
    }
    public Piece getPiece() {
        return piece;
    }
    public void setPiece(Piece piece) {
        this.piece = piece;
    }
    public Tile(boolean light, int x, int y) {
        setWidth(Main.TILE_SIZE);
        setHeight(Main.TILE_SIZE);
        relocate(x * Main.TILE_SIZE, y * Main.TILE_SIZE);
        setFill(light ? Color.valueOf("#fae7b5") : Color.valueOf("#755905")); //цвета клеток
    }
}