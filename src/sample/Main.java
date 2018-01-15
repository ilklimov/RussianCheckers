package sample;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.*;
import javafx.stage.Stage;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Ilya Klimov
 */

public class Main extends Application {
    public static final int TILE_SIZE = 80;
    public static final int WIDTH = 8;
    public static final int HEIGHT = 8;
    public int scoreOne = 0;
    public int scoreTwo = 0;
    private Tile[][] board = new Tile[WIDTH][HEIGHT];
    private Group tileGroup = new Group(); //доска
    private Group pieceGroup = new Group(); //шашки
    public PieceType wait = PieceType.WHITE;

    Map index = new HashMap<String,String>();
    TextArea leftText = new TextArea("      ХОД БЕЛЫХ:");
    Text scoreBlack = new Text(Integer.toString(scoreOne));
    Text scoreWhite = new Text(Integer.toString(scoreOne));
    TextFlow rightTop = new TextFlow();

    private Parent createContent() {
        GridPane root = new GridPane();
        root.setPrefSize(1200, 800); //размеры сетки
        Pane left = new Pane();
        left.setId("left");
        leftText.setPrefSize(200,640);
        left.setPrefSize(200,640);
        left.getChildren().addAll(leftText);
        Pane table = new Pane(); //создание доски
        table.setPrefSize(WIDTH * TILE_SIZE, HEIGHT * TILE_SIZE);
        table.getChildren().addAll(tileGroup, pieceGroup);
        BorderPane right = new BorderPane();
        right.setPrefSize(200,640);
        right.setId("right");
        rightTop.setId("right-top");
        scoreBlack.setId("score-black");
        rightTop.setPrefSize(200,300);
        rightTop.setTextAlignment(TextAlignment.CENTER);
        rightTop.getChildren().add(scoreBlack);
        TextFlow forRightBottom = new TextFlow();
        forRightBottom.setId("for-right-bottom");
        BorderPane rightBottom = new BorderPane();
        rightBottom.setBottom(forRightBottom);
        rightBottom.setId("right-bottom");
        scoreBlack.setId("score-white");
        rightBottom.setPrefSize(200,300);
        forRightBottom.setTextAlignment(TextAlignment.CENTER);
        forRightBottom.getChildren().add(scoreWhite);
        right.setTop(rightTop);
        right.setBottom(rightBottom);
        leftText.setId("left-text");
        root.setHgap(20); //отступы между строками и столбцами
        root.setVgap(20);
        root.setPadding(new Insets(20)); //отступы по краям
        root.add(left, 0, 0);
        root.add(table, 1, 0);
        root.add(right,2,0);
        index.put(Integer.toString(0),"A");
        index.put(Integer.toString(1),"B");
        index.put(Integer.toString(2),"C");
        index.put(Integer.toString(3),"D");
        index.put(Integer.toString(4),"E");
        index.put(Integer.toString(5),"F");
        index.put(Integer.toString(6),"G");
        index.put(Integer.toString(7),"H");
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                Tile tile = new Tile((x + y) % 2 == 0, x, y);
                board[x][y] = tile;
                tileGroup.getChildren().add(tile); //отрисовка клеток
                Piece piece = null;
                if (y <= 2 && (x + y) % 2 != 0) {
                    piece = makePiece(PieceType.BLACK, x, y);
                }
                if (y >= 5 && (x + y) % 2 != 0) {
                    piece = makePiece(PieceType.WHITE, x, y);
                }
                if (piece != null) {
                    tile.setPiece(piece);
                    pieceGroup.getChildren().add(piece);
                }
            }
        }
        return root;
    }
    private MoveResult tryMove(Piece piece, int newX, int newY) {
        MoveResult result;
        if (board[newX][newY].hasPiece() || (newX + newY) % 2 == 0 || piece.getType() != wait) {
            return new MoveResult(MoveType.NONE);
        }
        int x0 = toBoard(piece.getOldX());
        int y0 = toBoard(piece.getOldY());
        if (Math.abs(newX - x0) == 1 && newY - y0 == piece.getType().moveDir) {
            result =  new MoveResult(MoveType.NORMAL);
            if (newY == 7 && piece.getType()==PieceType.BLACK){
                result = new MoveResult(MoveType.KING,board[newX][newY].getPiece());
            }else if (newY == 0 && piece.getType()==PieceType.WHITE){
                result =  new MoveResult(MoveType.KING,board[newX][newY].getPiece());
            }
            return result;
        } else if (Math.abs(newX - x0) == 2 && newY - y0 == piece.getType().moveDir * 2) {
            int x1 = x0 + (newX - x0) / 2;
            int y1 = y0 + (newY - y0) / 2;
            if (board[x1][y1].hasPiece() && board[x1][y1].getPiece().getType() != piece.getType()) {
                return new MoveResult(MoveType.KILL, board[x1][y1].getPiece());
            }
        }
        return new MoveResult(MoveType.NONE);
    }
    private int toBoard(double pixel) {
        return (int)(pixel + TILE_SIZE / 2) / TILE_SIZE;
    }
    @Override
    public void start(Stage primaryStage) throws Exception {
        Scene scene = new Scene(createContent());
        primaryStage.setTitle("Русские шашки");
        primaryStage.setScene(scene);
        scene.getStylesheets().add(Main.class.getResource("Main.css").toExternalForm());
        primaryStage.show();
    }
    private Piece makePiece(PieceType type, int x, int y) {
        Piece piece = new Piece(type, x, y);
        piece.setOnMouseReleased(e -> {
            int newX = toBoard(piece.getLayoutX());
            int newY = toBoard(piece.getLayoutY());
            MoveResult result;
            if (newX < 0 || newY < 0 || newX >= WIDTH || newY >= HEIGHT) {
                result = new MoveResult(MoveType.NONE);
            } else {
                result = tryMove(piece, newX, newY);
            }
            int x0 = toBoard(piece.getOldX());
            int y0 = toBoard(piece.getOldY());
            switch (result.getType()) {
                case NONE:
                    piece.abortMove();
                    break;
                case NORMAL:
                    piece.move(newX, newY);
                    board[x0][y0].setPiece(null);
                    board[newX][newY].setPiece(piece);
                    if (wait==PieceType.BLACK){
                        wait = PieceType.WHITE;
                        leftText.appendText("\n"+index.get(Integer.toString(x0))+(y0+1)+":"+index.get(Integer.toString(newX))+(newY+1)+"\n         ХОД БЕЛЫХ:");
                    }else{
                        wait = PieceType.BLACK;
                        leftText.appendText("\n"+index.get(Integer.toString(x0))+(y0+1)+":"+index.get(Integer.toString(newX))+(newY+1)+"\n         ХОД ЧЕРНЫХ:");
                    }
                    break;
                case KILL:
                    piece.move(newX, newY);
                    board[x0][y0].setPiece(null);
                    board[newX][newY].setPiece(piece);
                    Piece otherPiece = result.getPiece();
                    board[toBoard(otherPiece.getOldX())][toBoard(otherPiece.getOldY())].setPiece(null);
                    if (otherPiece.getType()==PieceType.BLACK /*|| otherPiece.getType()==PieceType.BLACKKING*/){
                        scoreTwo++;
                        scoreWhite.setText(Integer.toString(scoreTwo));
                    }else{
                        scoreOne++;
                        scoreBlack.setText(Integer.toString(scoreOne));
                    }
                    if (wait==PieceType.BLACK){
                        wait = PieceType.WHITE;
                        leftText.appendText("\n"+index.get(Integer.toString(x0))+":"+(y0+1)+" -> "+index.get(Integer.toString(newX))+":"+(newY+1)+"\nсъедена шашка \nпротивника\n         ХОД БЕЛЫХ:");
                    }else{
                        wait = PieceType.BLACK;
                        leftText.appendText("\n"+index.get(Integer.toString(x0))+":"+(y0+1)+" -> "+index.get(Integer.toString(newX))+":"+(newY+1)+"\nсъедена шашка \nпротивника\n         ХОД ЧЕРНЫХ:");
                    }
                    pieceGroup.getChildren().remove(otherPiece);
                    break;
                case KING:
                    pieceGroup.getChildren().remove(piece);
                    board[x0][y0].setPiece(null);
                    if (wait==PieceType.BLACK){
                        wait = PieceType.WHITE;
                        pieceGroup.getChildren().remove(piece);
                        makePiece(PieceType.WHITEKING,newX,newY);
                        leftText.appendText("\n"+index.get(Integer.toString(x0))+":"+(y0+1)+" -> "+index.get(Integer.toString(newX))+":"+(newY+1)+"\nа вот и \nдамка\n         ХОД БЕЛЫХ:");
                    }else{
                        wait = PieceType.BLACK;
                        pieceGroup.getChildren().remove(piece);
                        makePiece(PieceType.BLACKKING,newX,newY);
                        leftText.appendText("\n"+index.get(Integer.toString(x0))+":"+(y0+1)+" -> "+index.get(Integer.toString(newX))+":"+(newY+1)+"\nа вот и \nдамка\n         ХОД ЧЕРНЫХ:");
                    }
                    board[newX][newY].setPiece(piece);
                    break;
            }
        });
        return piece;
    }
    public static void main(String[] args)  throws InterruptedException{ launch(args); }
}