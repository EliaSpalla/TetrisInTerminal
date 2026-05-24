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
    static Tetramino t, prossimo;

    public static void main(String[] args) throws IOException, InterruptedException {
        // inizializzazione
        t = new Tetramino(TipoTetramino.casuale());
        prossimo = new Tetramino(TipoTetramino.casuale());
        inserisciTetraminoInBuffer(); // Corretto: rimosso parametro t

        Terminal terminal = new DefaultTerminalFactory().createTerminal();
        Screen screen = new TerminalScreen(terminal);
        screen.startScreen();
        screen.setCursorPosition(null);

        // Passiamo l'oggetto screen al ciclo di gioco
        cicloDiGioco(screen);
    }

    public static void cicloDiGioco(Screen screen) throws IOException, InterruptedException {
        giocoInCorso = true;

        // ciclo di gioco
        while (giocoInCorso) {
            // stampa
            Thread.sleep(200);
            clearTerminal();
            stampaGriglia(); // Corretto: rimosso parametro prossimo

            // riceve input/modifiche
            KeyStroke key = screen.pollInput();
            if (key != null) {
                if (key.getKeyType() == KeyType.Escape) {
                    giocoInCorso = false;
                    break;
                }
                if (key.getKeyType() == KeyType.ArrowUp) ruotaTetramino(); // Corretto: rimosso parametro t
                if (key.getKeyType() == KeyType.ArrowRight) muoviTetramino(1); // Corretto: rimosso parametro t
                if (key.getKeyType() == KeyType.ArrowLeft) muoviTetramino(-1); // Corretto: rimosso parametro t
                if (key.getKeyType() == KeyType.ArrowDown) abbassaTetramino();
	    }

            // a causa della ripetizione automatica della tastiera
            while (screen.pollInput() != null) {
                // Continua a ciclare a vuoto finché il buffer non è pulito
            }

            if (!checkGravita()){
		t = prossimo;
                prossimo = new Tetramino(TipoTetramino.casuale());
                inserisciTetraminoInBuffer();
	    }
        }

        screen.stopScreen();
    }

    public static void abbassaTetramino(){
	while(checkGravita()){}
    }

    public static void muoviTetramino(int direzione) {
        rimuoviTetramino(); // Corretto: rimosso parametro t
        if (check(yTetramino, xTetramino + direzione)) { // Corretto: rimossi parametri t e corretta firma check
            xTetramino += direzione;
        }
        aggiungiTetramino(); // Corretto: rimosso parametro t
    }

    public static void ruotaTetramino() {
        rimuoviTetramino(); // Corretto: rimosso parametro t
        t.ruota(); // Ruota il pezzo originale

        // Se la nuova posizione NON è valida
        if (!check(yTetramino, xTetramino)) { // Corretto: passati solo 2 parametri
            // Ruota altre 3 volte per tornare alla posizione originale
            t.ruota();
            t.ruota();
            t.ruota();
        }
        aggiungiTetramino(); // Corretto: rimosso parametro t
    }

    public static boolean checkGravita() {
        // Controllo gravità: prima rimuoviamo per non collidere con se stessi durante il check
        rimuoviTetramino();
        if (!check(yTetramino + 1, xTetramino)) { // Corretto: passati solo 2 parametri
            // Se non può scendere, il pezzo si blocca: lo rimettiamo definitivamente nella griglia
            aggiungiTetramino();

	    return false;
        } else {
            // Se la posizione è valida, riaggiungiamo e applichiamo la gravità
            aggiungiTetramino();
            applicaGravita(); // Corretto: rimosso parametro t
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
        // Calcola la coordinata X per centrare il pezzo nella griglia
        if (t.tipo.name().equals("I")) { // Corretto controllo tipo enum
            xTetramino = LUNGHEZZAGRIGLIA / 2 - 2;
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
        aggiungiTetramino();
    }

    public static boolean check(int nuovaY, int nuovaX) {
        // Cerca SOLO se i blocchi vanno a collidere, senza modificare la griglia
        for (int i = 0; i < t.forma.length; i++) {
            for (int j = 0; j < t.forma[i].length; j++) {
                if (t.forma[i][j]) {
                    int rigaControllo = nuovaY + i;
                    int colonnaControllo = nuovaX + j;

                    // Controlla i bordi della griglia
                    if (rigaControllo >= ALTEZZAGRIGLIA + ALTEZZABUFFER ||
                        colonnaControllo < 0 ||
                        colonnaControllo >= LUNGHEZZAGRIGLIA) {
                        return false;
                    }

                    // Controlla se la cella è occupata da un altro blocco
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

    public static void applicaGravita() { // Corretto: rimosso parametro t nella firma
        rimuoviTetramino(); // Corretto: rimosso parametro t
        yTetramino++;
        aggiungiTetramino(); // Corretto: rimosso parametro t
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
