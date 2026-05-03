import java.util.Random;
import java.util.Scanner;
import java.io.IOException;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.TextColor;
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

    public static void main(String[] args) throws IOException, InterruptedException {
        // inizializzazione
        Tetramino t = new Tetramino(TipoTetramino.casuale());
        Tetramino prossimo = new Tetramino(TipoTetramino.casuale());
        inserisciTetraminoInBuffer(t);

        Terminal terminal = new DefaultTerminalFactory().createTerminal();
        Screen screen = new TerminalScreen(terminal);
        screen.startScreen();
        screen.setCursorPosition(null);

        // Passiamo l'oggetto screen al ciclo di gioco
        cicloDiGioco(t, prossimo, screen);
    }

    public static void cicloDiGioco(Tetramino t, Tetramino prossimo, Screen screen) throws IOException, InterruptedException {
        giocoInCorso = true;

        // ciclo di gioco
        while (giocoInCorso) {
            // stampa
            Thread.sleep(200);
            clearTerminal();
            stampaGriglia(prossimo);

            // riceve input/modifiche - Ora usa lo screen passato come parametro
            KeyStroke key = screen.pollInput();
            if (key != null) {
                // Corretto KeyType.Escape (case sensitive)
                if (key.getKeyType() == KeyType.Escape) giocoInCorso = false;
		if (key.getKeyType()==KeyType.ArrowUp) ruotaTetramino(t);
            }

            if (!check(t, yTetramino + 1, xTetramino)) {
                t = prossimo;
                prossimo = new Tetramino(TipoTetramino.casuale());
                inserisciTetraminoInBuffer(t);
            } else {
                applicaGravita(t);
            }
        }

        screen.stopScreen();
    }

    public static void ruotaTetramino(Tetramino t) {
    	rimuoviTetramino(t); // Rimuovi il pezzo attuale dalla griglia
    	t.ruota(); // Ruota il pezzo originale

    	// Se la nuova posizione NON è valida
        if (!check(t, yTetramino, xTetramino)) {
            // Ruota altre 3 volte per tornare alla posizione originale
            t.ruota();
            t.ruota();
            t.ruota();
        }
        aggiungiTetramino(t); // Reinserisci il pezzo (originale o ripristinato)
    }

    public static void stampaGriglia(Tetramino successivo) {
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
        successivo.stampa();
    }

    public static void inserisciTetraminoInBuffer(Tetramino t) {
        // Calcola la coordinata X per centrare il pezzo nella griglia
        if (t.tipo.name().equals("I")) { // Corretto controllo tipo enum
            xTetramino = LUNGHEZZAGRIGLIA / 2 -2;
        } else {
            xTetramino = (LUNGHEZZAGRIGLIA - t.forma[0].length) / 2;
        }
        yTetramino = 0;

        // 1. Controllo Collisioni (Game Over)
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
        // 2. Inserimento Effettivo
        aggiungiTetramino(t);
    }

    public static boolean check(Tetramino t, int nuovaY, int nuovaX) {
        rimuoviTetramino(t);

        for (int i = 0; i < t.forma.length; i++) {
            for (int j = 0; j < t.forma[i].length; j++) {
                if (t.forma[i][j]) {
                    int rigaControllo = nuovaY + i;
                    int colonnaControllo = nuovaX + j;

                    if (rigaControllo >= ALTEZZAGRIGLIA + ALTEZZABUFFER ||
                        colonnaControllo < 0 ||
                        colonnaControllo >= LUNGHEZZAGRIGLIA) {
                        aggiungiTetramino(t);
                        return false;
                    }

                    if (griglia[rigaControllo][colonnaControllo] != null) {
                        aggiungiTetramino(t);
                        return false;
                    }
                }
            }
        }
        aggiungiTetramino(t);
        return true;
    }

    public static void movimento(Tetramino t, int direzione) {
        if (direzione == 0) veloceGiù(t);
        xTetramino += direzione;
    }

    public static void veloceGiù(Tetramino t) {
    }

    public static void rimuoviTetramino(Tetramino t) {
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

    public static void aggiungiTetramino(Tetramino t) {
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

    public static void applicaGravita(Tetramino t) {
        rimuoviTetramino(t);
        yTetramino++;
        aggiungiTetramino(t);
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
