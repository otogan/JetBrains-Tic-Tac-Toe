package tictactoe;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // write your code here
        Game game = new Game();
        game.printField();
        do {
            Coordinates point;
            do {
                point = Input.askCoordinates("Enter the coordinates: ");
                if (point == null) {
                    System.out.println("You should enter numbers!");
                } else if (point.getX() < 0 || 3 < point.getX() || point.getY() < 0 || 3 < point.getY()) {
                    System.out.println("Coordinates should be from 1 to 3!");
                    point = null;
                } else if (!game.placeNext(point)) {
                    System.out.println("This cell is occupied! Choose another one!");
                    point = null;
                }
            } while (point == null);
            game.printField();
        } while (game.getStatus().equals(Game.Status.NOT_FINISHED));
        game.printStatus();
    }
}

class Input {
    private static final Scanner scanner;

    static {
        scanner = new Scanner(System.in);
    }

    private static void ask(String question) {
        System.out.print(question);
    }

    public static String askInput(String question) {
        ask(question);
        return scanner.nextLine();
    }

    public static Coordinates askCoordinates(String question) {
        ask(question);
        int x, y;
        if (scanner.hasNextInt()) {
            x = scanner.nextInt();
        } else {
            scanner.nextLine();
            return null;
        }
        if (scanner.hasNextInt()) {
            y = scanner.nextInt();
        } else {
            scanner.nextLine();
            return null;
        }
        return new Coordinates(x, y);
    }
}

class Coordinates {
    private final int x;
    private final int y;

    public Coordinates(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Coordinates that = (Coordinates) o;

        if (x != that.x) return false;
        return y == that.y;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }
}

class Game {
    private Piece nextPiece;
    private final Map<Coordinates, Piece> map = new HashMap<>();
    private final Map<Integer, Streak> rows = new HashMap<>();
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

    public enum Status {
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
        X("X"), O("O"), E(" "), D("D");

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

    public Game() {
        this.nextPiece = Piece.X;
        initMap();
    }

    public Game(String input) {
        this();
        int x;
        int y;
        int count = 0;
        while (count < input.length() && count < 9) {
            x = count / 3 + 1;
            y = count % 3 + 1;
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

    public Status getStatus() {
        return status;
    }

    private void place(int x, int y, Piece piece) {
        place(new Coordinates(x, y), piece);
    }

    private void place(Coordinates coordinates, Piece piece) {
        map.put(coordinates, piece);
        piece.place();
    }

    private void placePiece(int x, int y, Piece piece) {
        placePiece(new Coordinates(x, y), piece);
    }

    private void placePiece(Coordinates coordinates, Piece piece) {
        place(coordinates, piece);
        Piece.E.remove();
        validateStreaks(coordinates, piece);
    }

    private void validateStreaks(Coordinates coordinates, Piece piece) {
        int x = coordinates.getX();
        int y = coordinates.getY();
        rows.get(x).put(piece);
        cols.get(y).put(piece);
        if (x == 1 && y == 1 || x == 2 && y == 2 || x == 3 && y == 3) {
            diag.get(0).put(piece);
        }
        if (x == 1 && y == 3 || x == 2 && y == 2 || x == 3 && y == 1) {
            diag.get(1).put(piece);
        }
    }

    public boolean placeNext(Coordinates point) {
        Coordinates coordinates = convertPoint(point);
        if (!getPiece(coordinates).equals(Piece.E)) {
            return false;
        }
        placePiece(coordinates, nextPiece);
        analyze();
        nextPiece = nextPiece.equals(Piece.X) ? Piece.O : Piece.X;
        return true;
    }

    private static Coordinates convertPoint(Coordinates point) {
        return new Coordinates(4 - point.getY(), point.getX());
    }

    private Piece getPiece(int x, int y) {
        return getPiece(new Coordinates(x, y));
    }

    private Piece getPiece(Coordinates coordinates) {
        return map.get(coordinates);
    }

    private void initMap() {
        for (int i = 1; i <= 3; i++) {
            rows.put(i, new Streak());
            cols.put(i, new Streak());
            for (int j = 1; j <= 3; j++) {
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
        for (int i = 1; i <= 3; i++) {
            left();
            for (int j = 1; j <= 3; j++) {
                System.out.print(getPiece(i, j) + " ");
            }
            right();
        }
        line();
    }

    public void printStatus() {
        System.out.println(status);
    }

}