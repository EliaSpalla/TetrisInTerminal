import java.io.IOException;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
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
    static Tetramino t, prossimo;
    static int xTetramino, yTetramino;
    static boolean giocoInCorso;
    static TipoTetramino[][] griglia = new TipoTetramino[ALTEZZAGRIGLIA + ALTEZZABUFFER][LUNGHEZZAGRIGLIA];
    static int punteggio = 0;
    static int livello = 1;
    static int lineeEliminate = 0;
    static int[] puntiXLinee = new int[]{100, 300, 500, 800};


    void main() throws IOException, InterruptedException {
        // inizializzazione
        t = new Tetramino(TipoTetramino.casuale());
        prossimo = new Tetramino(TipoTetramino.casuale());
        inserisciTetraminoInBuffer();

        DefaultTerminalFactory factory = new DefaultTerminalFactory();
        // Dice a Lanterna di preferire l'emulatore grafico (la finestra Swing)
        factory.setPreferTerminalEmulator(true);
        // Dice a Lanterna di NON forzare il terminale testuale della console
        factory.setForceTextTerminal(false);

        Terminal terminal = factory.createTerminal();
        Screen screen = new TerminalScreen(terminal);
        screen.startScreen();
        screen.setCursorPosition(null);

        cicloDiGioco(screen);
    }

    public static void cicloDiGioco(Screen screen) throws IOException, InterruptedException {
        giocoInCorso = true;

        while (giocoInCorso) {
            Thread.sleep(200);

            screen.clear();
            stampaGriglia(screen);
            // Rende effettive le modifiche grafiche sulla finestra
            screen.refresh();

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

            if (!checkGravita()) {
                rimuoviLineeComplete();
                calcolaLivello();
                t = prossimo;
                prossimo = new Tetramino(TipoTetramino.casuale());
                inserisciTetraminoInBuffer();
            }
        }

        screen.stopScreen();
    }

    public static void calcolaLivello() {
        double numero = lineeEliminate / 10;
        int arrotondato = (int) Math.floor(numero);
        livello = arrotondato + 1;
    }

    public static void rimuoviLineeComplete() {
        int lineeRimosse = 0;
        for (int i = (ALTEZZAGRIGLIA + ALTEZZABUFFER) - 1; i >= 0; i--) {
            boolean checkLinea = true;
            for (int j = 0; j < LUNGHEZZAGRIGLIA; j++) {
                if (griglia[i][j] == null) {
                    checkLinea = false;
                    break;
                }
            }
            if (checkLinea) {
                rimuoviLinea(i);
                lineeRimosse++;
                i++;
            }
        }
        if (lineeRimosse > 0) {
            lineeEliminate += lineeRimosse;
            punteggio += puntiXLinee[lineeRimosse] * livello;
        }
    }

    public static void rimuoviLinea(int riga) {
        for (int j = 0; j < LUNGHEZZAGRIGLIA; j++) {
            griglia[riga][j] = null;
        }
        for (int i = riga; i > 0; i--) {
            griglia[i] = griglia[i - 1];
        }
        griglia[0] = new TipoTetramino[LUNGHEZZAGRIGLIA];
    }

    public static void abbassaTetramino() {
        while (checkGravita()) {
        }
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

    public static void stampaGriglia(Screen screen) {
        TextGraphics graphics = screen.newTextGraphics();

        int rigaCorrente = 0;
        for (int i = 0; i < ALTEZZAGRIGLIA + ALTEZZABUFFER; i++) {
            graphics.putString(0, rigaCorrente, "<! ");
            int colonnaCorrente = 3;
            for (int j = 0; j < LUNGHEZZAGRIGLIA; j++) {
                if (griglia[i][j] != null) {
                    graphics.setForegroundColor(convertiColoreAnsi(griglia[i][j].colore));
                    graphics.putString(colonnaCorrente, rigaCorrente, "██");
                } else {
                    graphics.setForegroundColor(TextColor.ANSI.DEFAULT);
                    graphics.putString(colonnaCorrente, rigaCorrente, "..");
                }
                colonnaCorrente += 2;
            }
            graphics.setForegroundColor(TextColor.ANSI.DEFAULT);
            graphics.putString(colonnaCorrente, rigaCorrente, " !>");
            rigaCorrente++;
        }

        graphics.putString(0, rigaCorrente, "<! ");
        StringBuilder base = new StringBuilder();
        for (int i = 0; i < LUNGHEZZAGRIGLIA; i++) base.append("==");
        graphics.putString(3, rigaCorrente, base.toString());
        graphics.putString(3 + (LUNGHEZZAGRIGLIA * 2), rigaCorrente, " !>");

        // Pannello laterale (Colonna 28)
        int colonnaPannello = 28;
        graphics.setForegroundColor(TextColor.ANSI.DEFAULT);

        graphics.putString(colonnaPannello, 1, "PUNTI:   " + punteggio);
        graphics.putString(colonnaPannello, 2, "LIVELLO: " + livello);

        graphics.putString(colonnaPannello, 4, "**PROSSIMO**");

        int n = prossimo.forma.length;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (prossimo.forma[i][j]) {
                    graphics.setForegroundColor(convertiColoreAnsi(prossimo.tipo.colore));
                    graphics.putString(colonnaPannello + (j * 2), 6 + i, "██");
                } else {
                    graphics.setForegroundColor(TextColor.ANSI.DEFAULT);
                    graphics.putString(colonnaPannello + (j * 2), 6 + i, "  ");
                }
            }
        }

        graphics.setForegroundColor(TextColor.ANSI.DEFAULT);

        graphics.putString(colonnaPannello, 12, "ruota:         ^");
        graphics.putString(colonnaPannello, 13, "spostamento:  < >");
        graphics.putString(colonnaPannello, 14, "giù veloce:    v");
        graphics.putString(colonnaPannello, 15, "exit:        [esc]");
    }

    // Metodo per mappare i tuoi codici colore ANSI nei colori nativi di Lanterna
    private static TextColor convertiColoreAnsi(String coloreAnsi) {
        if (coloreAnsi.contains("[35m")) return TextColor.ANSI.MAGENTA;
        if (coloreAnsi.contains("[36m")) return TextColor.ANSI.CYAN;
        if (coloreAnsi.contains("[32m")) return TextColor.ANSI.GREEN;
        if (coloreAnsi.contains("[31m")) return TextColor.ANSI.RED;
        if (coloreAnsi.contains("[33m")) return TextColor.ANSI.YELLOW;
        if (coloreAnsi.contains("[34m")) return TextColor.ANSI.BLUE;
        return TextColor.ANSI.WHITE;
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
                    if (riga >= 0 && riga < (ALTEZZAGRIGLIA + ALTEZZABUFFER) && colonna >= 0 && colonna < LUNGHEZZAGRIGLIA) {
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
}