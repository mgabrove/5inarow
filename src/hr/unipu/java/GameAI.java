package hr.unipu.java;
/***
 * Klasa inteligencije AI igrača.
 */
public class GameAI {
    /*
    Redom, varijabla veličine ploče, igrača koji igra, slijedećeg igrača, predikcijske ploče i prve te druge
    koordinate točke najmanje vrijednosti gubitka (točka koju se igra).
     */
    static private int size;
    static private int player;
    static private int playerNext;
    static private int[][] boardPred = {};
    static private int minK;
    static private int minL;

    /***
    Funkcija povrata optimalne točke.
     */
    public int[] returnVal() {
        int[] valRet = new int[2];
        valRet[0] = this.minK;
        valRet[1] = this.minL;
        return valRet;
    }

    /***
     * Konstruktor klase inteligencije.
     * @param size - veličina ploče
     * @param player - igrač koji igra
     * @param board - cijelokupna ploča igre sa svim figurama
     * @param maxPlayer - maksimalni broj igrača
     * @param moves - broj već odigranih poteza
     */
    public GameAI(int size, int player, int[][] board, int maxPlayer, int moves) {
        this.size = size;
        this.player = player;
        /*
        Priprema slijedećeg igrača iz trenutnog i maksimalnog broja.
         */
        if(this.player == maxPlayer) {
            this.playerNext = 1;
        } else {
            if(moves <= maxPlayer) {
                this.playerNext = 1;
            } else {
                this.playerNext = this.player + 1;
            }
        }
        /*
        Stvaranje nove predikcijske ploče koja se koristi za logiku inteligencije.
         */
        this.boardPred = new int[size][size];
        for(int k=0; k<size; k++) {
            for (int l = 0; l < size; l++) {
                this.boardPred[k][l] = board[k][l];
            }
        }
        /*
        Prolazak kroz predikcijsku ploču te pretpostavljanje vrijednosti igranja figura na
        susjedna polja već figurama popunjenih polja.
         */
        for(int k=0; k<size; k++) {
            for(int l=0; l<size; l++) {
                if(this.boardPred[k][l] != 0) {
                    /*
                    Ako se ne radi o kutnoj vrijednosti, idi dalje u taj kut.
                     */
                    if(l != 0 && k != 0) {
                        if(this.boardPred[k-1][l-1] == 0) {
                            this.boardPred[k-1][l-1] = this.getPredStatus((k-1), (l-1));
                        }
                    }
                    if(l != (this.size-1) && k != (this.size-1)) {
                        if(this.boardPred[k+1][l+1] == 0) {
                            this.boardPred[k+1][l+1] = this.getPredStatus((k+1), (l+1));
                        }
                    }
                    if(k != 0 && l != (this.size-1)) {
                        if(this.boardPred[k-1][l+1] == 0) {
                            this.boardPred[k-1][l+1] = this.getPredStatus((k-1), (l+1));
                        }
                    }
                    if(l != 0 && k != (this.size-1)) {
                        if(this.boardPred[k+1][l-1] == 0) {
                            this.boardPred[k+1][l-1] = this.getPredStatus((k+1), (l-1));
                        }
                    }
                    /*
                    Ako se ne radi o rubnoj vrijednosti, idi dalje ka rubu.
                     */
                    if(l != 0) {
                        if(this.boardPred[k][l-1] == 0) {
                            this.boardPred[k][l-1] = this.getPredStatus(k, (l-1));
                        }
                    }
                    if(l != (this.size-1)) {
                        if(this.boardPred[k][l+1] == 0) {
                            this.boardPred[k][l+1] = this.getPredStatus(k, (l+1));
                        }
                    }
                    if(k != 0) {
                        if(this.boardPred[k-1][l] == 0) {
                            this.boardPred[k-1][l] = this.getPredStatus((k-1), l);
                        }
                    }
                    if(k != (this.size-1)) {
                        if(this.boardPred[k+1][l] == 0) {
                            this.boardPred[k+1][l] = this.getPredStatus((k+1), l);
                        }
                    }
                    /*
                    Primjer rada svakog od prethodnih 8 if-ova. XX je promatrana točka.
                    Broj označava redni broj if-a koji provjerava postojanje čelije polja ploče.

                    [00 00 00 00 00]
                    [00 +1 +5 +3 00]
                    [00 +7 XX +8 00]
                    [00 +4 +6 +2 00]
                    [00 00 00 00 00]
                     */
                }
            }
        }
        /*
        Spremanje koordinata pozicije za igru.
         */
        int minK = 0;
        int minL = 0;
        int minVal = 0;
        for(int k=0; k<size; k++) {
            for(int l=0; l<size; l++) {
                if(minVal >= this.boardPred[k][l]) {
                    minK = k;
                    minL = l;
                    minVal = this.boardPred[k][l];
                }
            }
        }
        this.minK = minK;
        this.minL = minL;
    }
    /***
     * Metoda za izračun broja postavljenih figura u nizu.
     * @param r - vrijednost indeksa retka
     * @param dr - koeficijent rasta odnosno pada vrijednosti indeksta retka (1, -1)
     * @param c - vrijednost indeksa stupca
     * @param dc - koeficijent rasta odnosno pada vrijednosti indeksta retka (1, -1)
     * @param playerIn - igrač kojeg se promatra
     * @return - broj postavljenih figura u nizu
     */
    private int countAI(int r, int dr, int c, int dc, int playerIn) {
        for (int i=1; i<5; i++) {
            if (((r+dr*i) < 0 )|| ((c+dc*i) < 0) ||
                    ((r+dr*i) >= size) || ((c+dc*i) >= size)) return i-1; //Ako pozicija nije na ploči.
            if (boardPred[(r+dr*i)][(c+dc*i)] != playerIn) return i-1; //Ako je pozicija zauzeta od drugih ili prazna.
        }
        return 10;
    }

    /***
     * Metoda za izračun vrijednosti gubitka. Manja povratna vrijednost označava bolju poziciju za stavljanje figure.
     * @param row - vrijednost indeksa retka
     * @param col - vrijednost indeksa stupca
     * @return - vrijednost gubitka
     */
    public int getPredStatus(int row, int col) {
        /*
        Izračun vrijednosti countAI u svim smjerovima u odnosu na ispitivanu poziciju za promatranog igrača.
         */
        int leftRight, upDown, slash, backSlash, maxNumAI;
        upDown = countAI(row, 1, col, 0, player) + countAI(row, -1, col, 0, player) + 1;
        leftRight = countAI(row, 0, col, 1, player) + countAI(row, 0, col, -1, player) + 1;
        backSlash = countAI(row, 1, col, 1, player) + countAI(row, -1, col, -1, player) + 1;
        slash = countAI(row, 1, col, -1, player) + countAI(row, -1, col, 1, player) + 1;
        maxNumAI = upDown;
        if(maxNumAI <= leftRight) {
            maxNumAI = leftRight;
        }
        if(maxNumAI <= slash) {
            maxNumAI = slash;
        }
        if(maxNumAI <= backSlash) {
            maxNumAI = backSlash;
        }
        /*
        Izračun vrijednosti countAI u svim smjerovima u odnosu na ispitivanu poziciju za slijedečeg igrača.
         */
        int leftRightNext, upDownNext, slashNext, backSlashNext, maxNumNext;
        upDownNext = countAI(row, 1, col, 0, playerNext) + countAI(row, -1, col, 0, playerNext) + 1;
        leftRightNext = countAI(row, 0, col, 1, playerNext) + countAI(row, 0, col, -1, playerNext) + 1;
        backSlashNext = countAI(row, 1, col, 1, playerNext) + countAI(row, -1, col, -1, playerNext) + 1;
        slashNext = countAI(row, 1, col, -1, playerNext) + countAI(row, -1, col, 1, playerNext) + 1;
        maxNumNext = upDownNext;
        if(maxNumNext <= leftRightNext) {
            maxNumNext = leftRightNext;
        }
        if(maxNumNext <= slashNext) {
            maxNumNext = slashNext;
        }
        if(maxNumNext <= backSlashNext) {
            maxNumNext = backSlashNext;
        }
        /*
        Povratne vrijednosti odnosu na izračune. Inteligencija igra agresivno ako može napraviti veći niz od protivnika.
        Prebacuje se u obranu ako kaska za protivnikom.
         */
        if(maxNumAI >= 5) {
            return -8;
        } else if(maxNumNext >= 5) {
            return -7;
        } else if(maxNumAI == 4) {
            return -6;
        } else if(maxNumNext == 4) {
            return -5;
        } else if(maxNumAI == 3) {
            return -4;
        } else if(maxNumNext == 3) {
            return -3;
        } else if(maxNumAI == 2) {
            return -2;
        } else if(maxNumNext == 2) {
            return -1;
        }
        return 0;
    }
}
