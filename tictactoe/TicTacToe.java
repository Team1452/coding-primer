import java.util.Scanner;

class Main {

  public static void main(String[] args) {
    System.out.println("Running Tic-Tac-Toe!");

    char[][] board = new char[3][3];

    // Initialize board with empty slots.
    for (int row = 0; row < 3; row++) {
      for (int col = 0; col < 3; col++) {
        board[row][col] = '_';
      }
    }

    char player = 'X';

    Scanner scanner = new Scanner(System.in);

    while (true) {
      // Print board.
      for (int row = 0; row < 3; row++) {
        for (int col = 0; col < 3; col++) {
          System.out.print(board[row][col]);
          if (col != 2) System.out.print(" ");
        }
        System.out.println();
      }

      // Read player's move
      int row, col;
      System.out.println("Player " + player + "'s turn!");

      while (true) {
        System.out.print("Enter row [space] col: ");
        row = scanner.nextInt();
        col = scanner.nextInt();

        boolean outOfRange = row < 0 || row >= 3 || col < 0 || col >= 3;

        if (outOfRange) {
          System.out.println("Move must be within 3x3 board.");
          continue;
        }

        boolean available = board[row][col] == '_';

        if (!available) {
          System.out.println("Slot already taken!");
          continue;
        }

        // Move is valid. Exit loop and process turn.
        break;
      }

      // Make move on board.
      board[row][col] = player;

      // Check if player won.
      boolean won = false;

      // Check any filled rows.
      for (int rowI = 0; rowI < 3; rowI++) {
        if (
          board[rowI][0] == player &&
          board[rowI][1] == player &&
          board[rowI][2] == player
        ) {
          won = true;
          break;
        }
      }

      // Columns.
      for (int colI = 0; colI < 3; colI++) {
        if (
          board[0][colI] == player &&
          board[1][colI] == player &&
          board[2][colI] == player
        ) {
          won = true;
          break;
        }
      }

      // Diagonals.

      // Top-left
      if (
        board[0][0] == player && board[1][1] == player && board[2][2] == player
      ) {
        won = true;
      }

      // Top-right
      if (
        board[0][2] == player && board[1][1] == player && board[2][0] == player
      ) {
        won = true;
      }

      // If the player *has* won by any of the above
      // conditions, announce them as the winner, break
      // out of the loop and end the game.
      if (won) {
        System.out.println("Player " + player + " has won!");
        break;
      }

      // Since the player hasn't won (as the "break" statement
      // wasn't reached) now check if the board has any empty slots left,
      // labeled by an underscore '_'. If not, then no more moves can
      // be made and the game ends in a draw.
      boolean filled = true;
      for (int rowI = 0; rowI < 3; rowI++) {
        for (int colI = 0; colI < 3; colI++) {
          if (board[rowI][colI] == '_') filled = false;
        }
      }

      if (filled) {
        System.out.println("The board is filled up. It's a draw!");
        break;
      }

      // Alternate player.
      if (player == 'X') player = 'O'; else player = 'X';
    }

    // We've now broken out of the game loop
    // and can "clean up", in this case that means
    // closing the Scanner to free up the resources
    // it's using (System.in).
    // Technically, this isn't necessary since the program
    // will end after this line anyway, and your operating
    // system will free up the resources it was using
    // (which includes System.in). But calling .close()
    // on an I/O class like this *would* be necessary
    // to avoid a memory leak (i.e. wasting system resources
    // on something we don't need anymore) or if we wanted
    // to use that resource in a different context.
    // Either way, it's always "best practice" to free up stuff
    // you're not using.
    scanner.close();
  }
}
