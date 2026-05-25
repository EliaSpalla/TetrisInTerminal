import java.io.IOException;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

public class Main {

    final static int ALTEZZABUFFER = 3;
    final static int ALTEZZAGRIGLIA = 20;
    final static int LUNGHEZZAGRIGLIA = 10;
    static int xTetramino, yTetramino;
    static boolean giocoInCorso;
    static TipoTetramino[][] griglia = new TipoTetramino[ALTEZZAGRIGLIA + ALTEZZABUFFER][LUNGHEZZAGRIGLIA];
    static int punteggio = 0;
    static Tetramino t, prossimo;

    public static void main(String[] args) throws IOException, InterruptedException {
        // inizializzazione
        t = new Tetramino(TipoTetramino.casuale());
        prossimo = new Tetramino(TipoTetramino.casuale());
        inserisciTetraminoInBuffer();

        Terminal terminal = new DefaultTerminalFactory().createTerminal();
        Screen screen = new TerminalScreen(terminal);
        screen.startScreen();
        screen.setCursorPosition(null);

        cicloDiGioco(screen);
    }

    public static void cicloDiGioco(Screen screen) throws IOException, InterruptedException {
        giocoInCorso = true;

        while (giocoInCorso) {
            Thread.sleep(200);
            clearTerminal();
            stampaGriglia();

            KeyStroke key = screen.pollInput();
            if (key != null) {
                if (key.getKeyType() == KeyType.Escape) {
                    giocoInCorso = false;
                    break;
                }
                if (key.getKeyType() == KeyType.ArrowUp) ruotaTetramino();
                if (key.getKeyType() == KeyType.ArrowRight) muoviTetramino(1);
                if (key.getKeyType() == KeyType.ArrowLeft) muoviTetramino(-1);
                if (key.getKeyType() == KeyType.ArrowDown) abbassaTetramino();
            }

            while (screen.pollInput() != null) {
                // Svuota buffer per auto-repeat tastiera
            }

            if (!checkGravita()){
                rimuoviLineeComplete();
                t = prossimo;
                prossimo = new Tetramino(TipoTetramino.casuale());
                inserisciTetraminoInBuffer();
            }
        }

        screen.stopScreen();
    }

    public static void rimuoviLineeComplete() {
        // Parte dal fondo della griglia e sale (include la riga 0 con >= 0)
        for (int i = (ALTEZZAGRIGLIA + ALTEZZABUFFER) - 1; i >= 0; i--) {
            boolean checkLinea = true;
            // Controlla tutte le colonne (include la colonna 0 con j >= 0)
            for (int j = 0; j < LUNGHEZZAGRIGLIA; j++) {
                if (griglia[i][j] == null) {
                    checkLinea = false;
                    break;
                }
            }
            if (checkLinea) {
                rimuoviLinea(i);
                // Incrementa i per ricontrollare la stessa riga dopo lo slittamento
                i++;
            }
        }
    }

    public static void rimuoviLinea(int riga) {
        // Svuota la riga completata
        for (int j = 0; j < LUNGHEZZAGRIGLIA; j++) {
            griglia[riga][j] = null;
        }
        // Fa slittare tutte le righe sovrastanti verso il basso
        for (int i = riga; i > 0; i--) {
            griglia[i] = griglia[i - 1];
        }
        // Crea una nuova riga vuota in cima alla griglia (riga 0)
        griglia[0] = new TipoTetramino[LUNGHEZZAGRIGLIA];
    }

    public static void abbassaTetramino(){
        while(checkGravita()){}
    }

    public static void muoviTetramino(int direzione) {
        rimuoviTetramino();
        if (check(yTetramino, xTetramino + direzione)) {
            xTetramino += direzione;
        }
        aggiungiTetramino();
    }

    public static void ruotaTetramino() {
        rimuoviTetramino();
        t.ruota();

        if (!check(yTetramino, xTetramino)) {
            t.ruota();
            t.ruota();
            t.ruota();
        }
        aggiungiTetramino();
    }

    public static boolean checkGravita() {
        rimuoviTetramino();
        if (!check(yTetramino + 1, xTetramino)) {
            aggiungiTetramino();
            return false;
        } else {
            aggiungiTetramino();
            applicaGravita();
            return true;
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

        System.out.println("\n**Successivo**");
        prossimo.stampa();
    }

    public static void inserisciTetraminoInBuffer() {
        if (t.tipo.name().equals("I")) {
            xTetramino = LUNGHEZZAGRIGLIA / 2 - 2;
        } else {
            xTetramino = (LUNGHEZZAGRIGLIA - t.forma[0].length) / 2;
        }
        yTetramino = 0;

        for (int i = 0; i < t.forma.length; i++) {
            for (int j = 0; j < t.forma[i].length; j++) {
                if (t.forma[i][j]) {
                    if (griglia[yTetramino + i][xTetramino + j] != null) {
                        giocoInCorso = false;
                        return;
                    }
                }
            }
        }
        aggiungiTetramino();
    }

    public static boolean check(int nuovaY, int nuovaX) {
        for (int i = 0; i < t.forma.length; i++) {
            for (int j = 0; j < t.forma[i].length; j++) {
                if (t.forma[i][j]) {
                    int rigaControllo = nuovaY + i;
                    int colonnaControllo = nuovaX + j;

                    if (rigaControllo >= ALTEZZAGRIGLIA + ALTEZZABUFFER ||
                            colonnaControllo < 0 ||
                            colonnaControllo >= LUNGHEZZAGRIGLIA) {
                        return false;
                    }

                    if (griglia[rigaControllo][colonnaControllo] != null) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public static void rimuoviTetramino() {
        for (int i = 0; i < t.forma.length; i++) {
            for (int j = 0; j < t.forma[i].length; j++) {
                if (t.forma[i][j]) {
                    int riga = yTetramino + i;
                    int colonna = xTetramino + j;
                    if (riga >= 0 && riga < (ALTEZZAGRIGLIA + ALTEZZABUFFER) &&
                            colonna >= 0 && colonna < LUNGHEZZAGRIGLIA) {
                        griglia[riga][colonna] = null;
                    }
                }
            }
        }
    }

    public static void aggiungiTetramino() {
        for (int i = 0; i < t.forma.length; i++) {
            for (int j = 0; j < t.forma[i].length; j++) {
                if (t.forma[i][j]) {
                    int riga = yTetramino + i;
                    int colonna = xTetramino + j;
                    if (riga >= 0 && riga < (ALTEZZAGRIGLIA + ALTEZZABUFFER) &&
                            colonna >= 0 && colonna < LUNGHEZZAGRIGLIA) {
                        griglia[riga][colonna] = t.tipo;
                    }
                }
            }
        }
    }

    public static void applicaGravita() {
        rimuoviTetramino();
        yTetramino++;
        aggiungiTetramino();
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