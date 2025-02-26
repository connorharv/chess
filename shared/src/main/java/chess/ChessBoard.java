package chess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import chess.ChessPiece.PieceType;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    private ChessPiece[][] board;

    public ChessBoard() {
        board = new ChessPiece[8][8];
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        board[rowToArray(position.getRow())][colToArray(position.getColumn())] = piece;
    }

    /**
     * 
     * @param board the board to set
     * @return void
     */
    public void setBoard(ChessPiece[][] otherBoard){
        this.board = otherBoard;
    }

    /**
     * Returns the array of the pieces
     * @return ChessPiece[][]
     */
    public ChessPiece[][] getBoard(){
        return board;
    }


    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return board[rowToArray(position.getRow())][colToArray(position.getColumn())];
    }

    /**
     * 
     * @param i row index (array indexing)
     * @param j col index (array indexing)
     * @return
     */
    public ChessPiece getPiecebyIndex(int i, int j){
        return board[i][j];
    }

    /**
     * Moves a chess piece on the chessboard
     * 
     * @param move the move to take
     */
    public void movePiece(ChessMove move){

        ChessPiece piece = getPiece(move.getStartPosition());
        ChessGame.TeamColor teamColor = piece.getTeamColor();

        int iOrig = rowToArray(move.getStartPosition().getRow());
        int jOrig = colToArray(move.getStartPosition().getColumn());
        int iFini = rowToArray(move.getEndPosition().getRow());
        int jFini = colToArray(move.getEndPosition().getColumn());

        if(move.getPromotionPiece() != null){
            board[iFini][jFini] = new ChessPiece(teamColor, move.getPromotionPiece());
        }
        else{
            board[iFini][jFini] =  board[iOrig][jOrig];
        }
        board[iOrig][jOrig] = null;
    }

    /**
     * 
     * @param teamColor color to check for
     * @return locations of team's chess pieces
     */
    public ArrayList<ChessPosition> getTeamPositions(ChessGame.TeamColor teamColor){
        ArrayList<ChessPosition> teamPositions = new ArrayList<>();
        for(int i = 0; i < 8; i++){     
            for(int j = 0; j < 8; j++){
                if(board[i][j] != null && board[i][j].getTeamColor() == teamColor){
                    teamPositions.add(new ChessPosition(arrayToRow(i), arrayToCol(j)));
                }
            }
        }
        return teamPositions;
    }

    /**
     * 
     * @param teamColor team color to find king of
     * @return position (row,col) of king
     */
    public ChessPosition getKingLocation(ChessGame.TeamColor teamColor){
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                ChessPiece piece = board[i][j];
                if(piece != null && piece.getPieceType() == PieceType.KING && piece.getTeamColor() == teamColor){
                    return new ChessPosition(arrayToRow(i), arrayToCol(j));
                }
            }
        }
        return null;
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        // Order LR -> rook, knight, bishop, queen, king, bishop, knight, rook
        // Set black special
        board[0][0] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
        board[0][1] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
        board[0][2] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
        board[0][3] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN);
        board[0][4] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING);
        board[0][5] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
        board[0][6] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
        board[0][7] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
        // Set black pawns
        for(int i = 0; i < 8; i++) {
            board[1][i] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
        }
        // Set white pawns
        for(int i = 0; i < 8; i++) {
            board[6][i] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        }
        // set white special
        board[7][0] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
        board[7][1] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
        board[7][2] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
        board[7][3] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN);
        board[7][4] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING);
        board[7][5] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
        board[7][6] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
        board[7][7] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
    }

    private int rowToArray(int row){
        return 8-row;
    }

    private int arrayToRow(int i){
        return 8-i;
    }

    private int colToArray(int col){
        return col-1;
    }

    private int arrayToCol(int j){
        return j+1;
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }

    @Override
    public String toString() {
        String str = "";
        for(int i = 0; i < 8; i++) {
            str += "|";
            for(int j = 0; j < 8; j++) {
                if(board[i][j] != null) {
                    str += board[i][j].toString();
                }
                else{
                    str += " ";
                }
                str += "|";
            }
            str += "\n";
        }
        return str;
    }
}
