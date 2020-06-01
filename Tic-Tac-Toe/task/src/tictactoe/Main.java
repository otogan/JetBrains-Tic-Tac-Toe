package tictactoe;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // write your code here
        Field game = new Field(Input.ask("Enter cells: ").toUpperCase());
        game.printField();
        game.printStatus();
    }
}

class Input {
    private static final Scanner scanner;
    static {
        scanner = new Scanner(System.in);
    }
    public static String ask(String question) {
        System.out.print(question);
        return scanner.nextLine();
    }
}

class Field {
    private Piece[][] map;
    private final HashMap<Integer, Streak> rows = new HashMap<>();
    private final Map<Integer, Streak> cols = new HashMap<>();
    private final Map<Integer, Streak> diag = new HashMap<>();
    private Status status = Status.NOT_FINISHED;

    class Streak {
        Piece piece;
        int count;

        Streak() {
            this.piece = Piece.E;
        }

        void put(Piece piece) {
            if (this.piece.equals(Piece.E)) {
                this.piece = piece;
                count = 1;
            } else if (this.piece.equals(piece)) {
                count++;
            } else {
                this.piece = Piece.D;
                count = 0;
            }
        }
    }

    private enum Status {
        NOT_FINISHED("Game not finished"),
        DRAW("Draw"),
        X_WINS("X wins"),
        O_WINS("O wins"),
        IMPOSSIBLE("Impossible");

        private final String value;

        Status(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    private enum Piece {
        X("X"), O("O"), E("_"), D("D");

        private final String value;
        private int count = 0;
        private boolean wins;

        Piece(String value) {
            this.value = value;
        }

        public void place() {
            count++;
        }

        public void remove() {
            count--;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    public Field(String input) {
        initMap();
        int x;
        int y;
        int count = 0;
        while (count < input.length() && count < 9) {
            x = count / 3;
            y = count % 3;
            char c = input.charAt(count);
            count++;
            switch (c) {
                case 'X':
                    placePiece(x, y, Piece.X);
                    break;
                case 'O':
                    placePiece(x, y, Piece.O);
                    break;
            }
        }
        analyze();
    }

    private void place(int x, int y, Piece piece) {
        map[x][y] = piece;
        piece.place();
    }

    private void placePiece(int x, int y, Piece piece) {
        place(x, y, piece);
        Piece.E.remove();
        rows.get(x).put(piece);
        cols.get(y).put(piece);
        if (x == 0 && y == 0 || x == 1 && y == 1 || x == 2 && y == 2) {
            diag.get(0).put(piece);
        }
        if (x == 0 && y == 2 || x == 1 && y == 1 || x == 2 && y == 0) {
            diag.get(1).put(piece);
        }
    }

    private void initMap() {
        map = new Piece[3][3];
        for (int i = 0; i < 3; i++) {
            rows.put(i, new Streak());
            cols.put(i, new Streak());
            for (int j = 0; j < 3; j++) {
                place(i, j, Piece.E);
            }
        }
        diag.put(0, new Streak());
        diag.put(1, new Streak());
    }

    private void analyze() {
        checkStreaks(rows.values());
        checkStreaks(cols.values());
        checkStreaks(diag.values());

        if (Piece.X.wins && Piece.O.wins || Math.abs(Piece.X.count - Piece.O.count) > 1) {
            status = Status.IMPOSSIBLE;
            return;
        }

        if (Piece.X.wins) {
            status = Status.X_WINS;
        } else if (Piece.O.wins) {
            status = Status.O_WINS;
        } else if (Piece.E.count == 0) {
            status = Status.DRAW;
        }
    }

    private void checkStreaks(Collection<Streak> streaks) {
        streaks.forEach(streak -> {
            if (streak.count == 3) streak.piece.wins = true;
        });
    }

    private void line() {
        System.out.println("---------");
    }

    private void left() {
        System.out.print("| ");
    }

    private void right() {
        System.out.println("|");
    }

    public void printField() {
        line();
        for (int i = 0; i < 3; i++) {
            left();
            for (int j = 0; j < 3; j++) {
                System.out.print(map[i][j] + " ");
            }
            right();
        }
        line();
    }

    public void printStatus() {
        System.out.println(status);
    }

}