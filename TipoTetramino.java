import java.util.Random;

public enum TipoTetramino {
    T  (new boolean[][]{{false,false,false},{false,true,false},{true,true,true}},   "\033[35m"), // magenta
    I  (new boolean[][]{{true,true,true,true}},                                     "\033[36m"), // cyan
    S  (new boolean[][]{{false,false,false},{false,true,true},{true,true,false}},   "\033[32m"), // verde
    S_REV(new boolean[][]{{false,false,false},{true,true,false},{false,true,true}}, "\033[31m"), // rosso
    L  (new boolean[][]{{false,false,false},{false,false,true},{true,true,true}},   "\033[33m"), // giallo
    L_REV(new boolean[][]{{false,false,false},{true,false,false},{true,true,true}}, "\033[34m"), // blu
    Q  (new boolean[][]{{true,true},{true,true}},                                   "\033[37m"); // bianco

    public final boolean[][] forma;
    public final String colore;
    public static final String RESET = "\033[0m";

    private static final TipoTetramino[] valori = values();
    private static final Random rand = new Random();

    TipoTetramino(boolean[][] forma, String colore) {
        this.forma = forma;
        this.colore = colore;
    }

    public static TipoTetramino casuale() {
        return valori[rand.nextInt(valori.length)];
    }
}