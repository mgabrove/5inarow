package hr.unipu.java;
/***
 * Klasa GameLogic. Klasa cjelokupne igre Five in a Row bez dijela računalne inteligencije.
 */
class GameLogic {
    /*
    Redom broj redaka, broj stupaca, ploča igre, igrač na redu, broj napravljenih poteza, x i y koordinate posljednje
    odigrane figure, polje koordinata početka i kraja pobjedničkog niza.
     */
    private int size;
    private int maxPlayer;
    private int[][] board;
    private int nextPlayer;
    private int moves = 0;
    private int lastX, lastY;
    private int[][] pocZav = null;
    /*
    Postavljanje statičkih oznaka stanja čelija igrače ploče prazno, oznake igrača jedan te oznake remia.
    */
    public static final int EMPTY = 0;
    public static final int PLAYER1 = 1;
    public static final int TIE = -1;
    /***
     * Konstruktor klase logike.
     * @param size - veličina ploče
     * @param maxPlayer - maksimalni broj igrača
     */
    public GameLogic(int size, int maxPlayer) {
        this.size = size;
        this.maxPlayer = maxPlayer;
        board = new int[size][size];
        reset(size, maxPlayer);
    }
    /***
     * @return - vrati igrača koji igra
     */
    public int getNextPlayer() { return nextPlayer; }
    /***
     * @return - vrati X koordinatu zadnje odigrane figure
     */
    public int getLastX() { return lastX; }
    /***
     * @return - vrati Y koordinatu zadnje odigrane figure
     */
    public int getLastY() { return lastY; }
    /***
     * @param r - vrijednost retka
     * @param c - vrijednost stupca
     * @return - vrati igrača koji je igrao na tom retku i stupcu
     */
    public int getPlayerAt(int r, int c) { return board[r][c]; }
    /***
     * @return - vrati polje koordinata početka i kraja pobjedničkog niza
     */
    public int[][] getPocZav() { return pocZav; }
    /***
     * Čišćenje ploče, vračanje u početno stanje.
     */
    public void reset(int size, int players) {
        this.size = size;
        this.maxPlayer = players;
        board = new int[size][size];
        for (int r=0; r<this.size; r++) {
            for (int c=0; c<this.size; c++) {
                board[r][c] = EMPTY;
            }
        }
        moves = 0;
        nextPlayer = PLAYER1;
    }
    /***
     * Postavljanje figure na ploču te prebacivanje na slijedečeg igrača.
     */
    public void move(int r, int c) {
        if(nextPlayer == 1) { // Ako je stvarni igrač na redu.
            assert board[r][c] == EMPTY;
            board[r][c] = nextPlayer; // Spremanje poteza.
        } else { // Ako je računalo na redu.
            GameAI gameAI = new GameAI(size, nextPlayer, board, maxPlayer, moves);
            board[gameAI.returnVal()[0]][gameAI.returnVal()[1]] = nextPlayer;  // Spremanje poteza.
            /*
            Spremanje posljednjih vrijednosti unosa za prikaz posljednjeg poteza računala.
             */
            lastX = gameAI.returnVal()[0];
            lastY = gameAI.returnVal()[1];
        }
        /*
        Prebacivanje na slijedečeg igrača.
         */
        if(nextPlayer == 1) {
            nextPlayer = 2;
        } else if(nextPlayer == 2) {
            if(maxPlayer > 2) {
                nextPlayer = 3;
            } else {
                nextPlayer = 1;
            }
        } else if(nextPlayer == 3) {
            if(maxPlayer > 3) {
                nextPlayer = 4;
            } else {
                nextPlayer = 1;
            }
        } else {
            nextPlayer = 1;
        }
        moves++; // Broj poteza izmjenjen za +1.
    }
    /***
     * Metoda za otkrivanje postojanja niza od pet istih figura te spremanje istog niza za kasniju vizualizaciju.
     * @param r - vrijednost indeksa retka
     * @param dr - koeficijent rasta odnosno pada vrijednosti indeksta retka (1, -1)
     * @param c - vrijednost indeksa stupca
     * @param dc - koeficijent rasta odnosno pada vrijednosti indeksta retka (1, -1)
     * @return - vrati je li pronađeno 5 figura u nizu
     */
    private boolean count5(int r, int dr, int c, int dc) {
        int player = board[r][c];
        int pocY = r;
        int pocX = c;
        int zavX = 0;
        int zavY = 0;
        for (int i=1; i<5; i++) { // Listaj usmjereno sa dr i dc.
            zavY = r+dr*i;
            zavX = c+dc*i;
            if (board[r+dr*i][c+dc*i] != player) return false; // Ako se na poziciji ne nalazi igrač nije pronađeno.
        }
        /*
        Kod pronalaska, spremi poziciju početne i završne figure.
         */
        pocZav = new int[2][2];
        pocZav[0][0] = pocX;
        pocZav[0][1] = pocY;
        pocZav[1][0] = zavX;
        pocZav[1][1] = zavY;
        return true;
    }
    /***
     * @return - vrati broj učinjenih poteza
     */
    public int getMoves() { return moves; }
    /***
     *
     * @return - vrati je li ostvarena pobjeda, neriješeni rezultat ili se igra dalje.
     */
    public int getGameStatus() {
        int row, col;
        for (row = 0; row < size; row++) {
            for (col = 0; col < size; col++) {
                int p = board[row][col];
                if (p != EMPTY) {
                    if (row < size-4) // Smjer gore.
                        if (count5(row, 1, col, 0)) return p;
                    if (col < size-4) { // Smjer desno.
                        if (count5(row, 0, col, 1))  return p;

                        if (row < size-4) { // Smjer dijagonalno gore desno.
                            if (count5(row, 1, col, 1)) return p;
                        }
                    }
                    if (col > 3 && row < size-4) { // Smjer dijagonalno gore lijevo.
                        if (count5(row, 1, col, -1)) return p;
                    }
                }
            }
        }
        if (moves == size*size) {
            return TIE; // Neriješeno.
        } else {
            return 0;  // Igra se dalje.
        }
    }
}