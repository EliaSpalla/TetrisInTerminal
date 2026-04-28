import java.util.Random;

public class Main {
    final static int ALTEZZABUFFER = 3;
    final static int ALTEZZAGRIGLIA = 20;
    final static int LUNGHEZZAGRIGLIA = 10;
    static TipoTetramino[][] griglia = new TipoTetramino[ALTEZZAGRIGLIA + ALTEZZABUFFER][LUNGHEZZAGRIGLIA];

    public static void main(String[] args) throws InterruptedException {
        Tetramino t = new Tetramino(TipoTetramino.casuale());
        inserisciTetraminoInBuffer(t);
        while (true) {
            Thread.sleep(200);
            clearTerminal();
            stampaGriglia();
            applicaGravita();
        }
    }

    public static void stampaGriglia() {
        for (int i = 0; i < ALTEZZAGRIGLIA + ALTEZZABUFFER; i++) {
            System.out.print("<! ");
            for (int j = 0; j < LUNGHEZZAGRIGLIA; j++) {
                if (griglia[i][j] != null)
                    System.out.print(griglia[i][j].colore + "██" + TipoTetramino.RESET);
                else
                    System.out.print("..");
            }
            System.out.println(" !>");
        }
        System.out.print("<! ");
        for (int i = 0; i < LUNGHEZZAGRIGLIA; i++) System.out.print("==");
        System.out.println(" !>");
    }

    public static void inserisciTetraminoInBuffer(Tetramino t) {
        int metaLunghezza = LUNGHEZZAGRIGLIA / 2;
        for (int i = 0; i < t.forma.length; i++)
            for (int j = 0; j < t.forma[0].length; j++)
                if (t.forma[i][j])
                    griglia[i][metaLunghezza + j] = t.tipo;
    }

    public static void gravitaNastroTrasportatore() {
        TipoTetramino[] ultimaRiga = griglia[ALTEZZAGRIGLIA + ALTEZZABUFFER - 1];
        for (int i = ALTEZZAGRIGLIA + ALTEZZABUFFER - 1; i > 0; i--)
            griglia[i] = griglia[i - 1];
        griglia[0] = ultimaRiga;
    }

    public static void applicaGravita() {
        for (int i = ALTEZZAGRIGLIA + ALTEZZABUFFER - 2; i >= 0; i--) {
            for (int j = 0; j < LUNGHEZZAGRIGLIA; j++) {
                if (griglia[i][j] != null && griglia[i + 1][j] == null) {
                    griglia[i + 1][j] = griglia[i][j];
                    griglia[i][j] = null;
                }
            }
        }
    }

    public static void clearTerminal() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            for (int i = 0; i < 50; i++) System.out.println();
        }
    }
}