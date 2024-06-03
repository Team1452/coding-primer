package tictactoe2;

import java.util.Scanner;

public class App {

  public static final char X = 'X';
  public static final char O = 'O';
  public static final char EMPTY = '_';

  private static class Position {

    int row, col;

    public Position(int row, int col) {
      this.row = row;
      this.col = col;
    }

    public int getRow() {
      return row;
    }

    public int getCol() {
      return col;
    }
  }

  public static Position promptPosition(Scanner scanner, Board board) {
    int row, col;

    while (true) {
      System.out.print("Enter row [space] col: ");

      row = scanner.nextInt();
      col = scanner.nextInt();

      if (!board.inRange(row, col)) {
        System.out.println("Move must be within 3x3 board.");
        continue;
      }

      if (board.get(row, col) != EMPTY) {
        System.out.println("Slot already taken!");
        continue;
      }

      break;
    }

    return new Position(row, col);
  }

  public static void main(String[] args) {
    Board board = new Board(3, 3);

    char player = X;
    Scanner scanner = new Scanner(System.in);

    System.out.println("Starting tic-tac-toe!");

    while (true) {
      board.print();

      System.out.println("Player " + player + "'s turn!");
      Position position = promptPosition(scanner, board);

      board.set(position.getRow(), position.getCol(), player);

      if (board.hasWon(player)) {
        board.print();
        System.out.println("Player " + player + " has won!");
        break;
      }

      if (board.full()) {
        board.print();
        System.out.println("It's a draw!");
        break;
      }

      player = player == X ? O : X;
    }

    scanner.close();
  }
}
