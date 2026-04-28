public class Tetramino {
    public boolean[][] forma;
    public final TipoTetramino tipo;

    public Tetramino(TipoTetramino tipo) {
        this.tipo = tipo;
        this.forma = tipo.forma;
    }

    public void ruota() {
        int n = forma.length;
        boolean[][] nuova = new boolean[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                nuova[j][n - 1 - i] = forma[i][j];
        forma = nuova;
    }
}