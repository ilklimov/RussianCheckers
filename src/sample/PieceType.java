package sample;

/**
 * @author Ilya Klimov
 */

public enum PieceType {
    BLACK(1), WHITE(-1), BLACKKING(2), WHITEKING(-2);
    final int moveDir;
    PieceType(int moveDir) {
        this.moveDir = moveDir;
    }
}