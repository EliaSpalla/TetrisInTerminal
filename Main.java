 import java.util.Random;

public class Main {
    final static int ALTEZZABUFFER = 3;
    final static int ALTEZZAGRIGLIA = 20;
    final static int LUNGHEZZAGRIGLIA = 10;
    static int xTetramino, yTetramino;
    static boolean giocoInCorso=true;
    static TipoTetramino[][] griglia = new TipoTetramino[ALTEZZAGRIGLIA + ALTEZZABUFFER][LUNGHEZZAGRIGLIA];

    public static void main(String[] args) throws InterruptedException {
        Tetramino t = new Tetramino(TipoTetramino.casuale());
	Tetramino prossimo=new Tetramino(TipoTetramino.casuale());
        inserisciTetraminoInBuffer(t);
        while (giocoInCorso) {
            Thread.sleep(200);
            clearTerminal();
            stampaGriglia(prossimo);
            if(!check(t)){
	    	t=prossimo;
		prossimo=new Tetramino(TipoTetramino.casuale());
		inserisciTetraminoInBuffer(t);
	    }else{applicaGravita(t);}
        }
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
	if(t.tipo.equals("I")){xTetramino=LUNGHEZZAGRIGLIA/2;}
 	else{xTetramino = (LUNGHEZZAGRIGLIA - t.forma[0].length) / 2;}
	 yTetramino = 0;

 	 // 1. Controllo Collisioni (Game Over)
  	 // Prima di inserire, verifichiamo che i blocchi occupati dal tetramino siano liberi nella griglia
       for (int i = 0; i < t.forma.length; i++) {
     	   for (int j = 0; j < t.forma[i].length; j++) {
                if (t.forma[i][j]) {
                   // Se la cella della griglia è già occupata, il gioco finisce
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

    public static boolean check(Tetramino t) {
        rimuoviTetramino(t);

	for (int i = 0; i < t.forma.length; i++) {
            for (int j = 0; j < t.forma[i].length; j++) {
                if (t.forma[i][j]) {
                    int nuovaY = yTetramino + i + 1; // Posizione dove andrebbe il blocco
                    int currentX = xTetramino + j;

                    // Controlla se tocca il fondo o un altro pezzo
                    if (nuovaY >= ALTEZZAGRIGLIA + ALTEZZABUFFER||griglia[nuovaY][currentX]!=null){
			aggiungiTetramino(t);
			return false;
		    }
                }
            }
        }
	aggiungiTetramino(t);
        return true;
    }

    public static void rimuoviTetramino(Tetramino t){
	for(int i=0;i<t.forma.length;i++){
	    for(int j=0;j<t.forma.length;j++){
		if (t.forma[i][j]) {
                    int riga = yTetramino + i;
                    int colonna = xTetramino + j;
                    // Protezione contro IndexOutOfBounds
                    if (riga >= 0 && riga < (ALTEZZAGRIGLIA + ALTEZZABUFFER) && 
                        colonna >= 0 && colonna < LUNGHEZZAGRIGLIA) {
                        griglia[riga][colonna] = null;
                    }
                }
	    }
	}
    }

    public static void aggiungiTetramino(Tetramino t){
	for(int i=0;i<t.forma.length;i++){
	    for(int j=0;j<t.forma.length;j++){
		if (t.forma[i][j]) {
                    int riga = yTetramino + i;
                    int colonna = xTetramino + j;
                    // Protezione contro IndexOutOfBounds
                    if (riga >= 0 && riga < (ALTEZZAGRIGLIA + ALTEZZABUFFER) && 
                        colonna >= 0 && colonna < LUNGHEZZAGRIGLIA) {
                        griglia[riga][colonna] = t.tipo;
                    }
                }
	    }
	}
    }

    public static void applicaGravita(Tetramino t) {
        // 1. Rimuovi il pezzo dalla posizione attuale
        for (int i = 0; i < t.forma.length; i++) {
            for (int j = 0; j < t.forma[i].length; j++) {
                if (t.forma[i][j]) griglia[yTetramino + i][xTetramino + j] = null;
            }
        }
        // 2. Aggiorna la coordinata
        yTetramino++;
        // 3. Disegna il pezzo nella nuova posizione
        for (int i = 0; i < t.forma.length; i++) {
            for (int j = 0; j < t.forma[i].length; j++) {
                if (t.forma[i][j]) griglia[yTetramino + i][xTetramino + j] = t.tipo;
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
