import java.util.Scanner;
import java.util.logging.Level;

class Main {
    public static void main(String[] args) {
        // put your code here
        Scanner scanner = new Scanner(System.in);
        int total = 0;
        while (scanner.hasNext()) {
            total += Level.parse(scanner.next().toUpperCase()).intValue();
        }
        System.out.println(total);
    }
}