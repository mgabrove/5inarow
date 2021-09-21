package hr.unipu.java;
/***
 * 5 in a Row, igra je nadahnuta mobilnim igrama sa starih Sony Ericsson mobitela.
 * Igra se sastoji od ploče na kojoj naizmjence igrači postavljaju svoje figurice.
 * Prvi igrač koji postavi 5 svojih figurica u neprekinuti niz je pobjednik.
 * Igra dopušta igranje na maloj, srednjoj i velikoj ploči.
 * Igranjem počinje igrač nakon čega se izmjenjuju od 1 do 3 računalnih igrača pokretanih jednostavnom inteligencijom.
 * Veličinu ploče te broj igrača korisnik sam može namještati kroz "New Game" meni.
 */
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import javax.swing.*;
/***
 * Klasa Game. Vanjska klasa cjelokupnog izvođenja programa.
 */
class Game {
    /*
    Okvir klase Game u kojemu se izvodi aplikacija.
     */
    static JFrame window = new JFrame("Five in a Row");
    /***
     * Glavna main funkcija. Instanciranje same aplikacije.
     * Namještanje osnovnih opcija okvira: zatvaranja programa, promjenjivosti, lokacije, dimenzije.
     */
    public static void main(String[] args) {
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setContentPane(new GameGUI());
        window.pack();
        window.setResizable(false);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        window.setLocation(dim.width/2-window.getSize().width/2, dim.height/2-window.getSize().height/2);
        window.setVisible(true);
    }
    /*
    Klasa GameGUI zadužena je za stvaranje cjelokupnog sučelja igre.
    */
    static class GameGUI extends JPanel {
        /*
        Postavljanje redom: veličine ploče u poljima, veličine polja, veličine ploče, maksimalnog broja igrača,
        grafičke ploče za grafički prikaz, tekstualnog polja za status igre, logičkog objekta zaduženog za
        brigu o cjelokupnoj logici, boolean varijable o stanju igre(gotovo ili nije), statičkih boja i imena
        pojedinih igrača.
        */
        private int size = 25;
        private int cellLen = 30;
        private int sizeLen = size * cellLen;
        private int maxPlayer = 2;
        private GraphicsPanel boardDisplay;
        private JTextField statusField = new JTextField();
        private GameLogic gameLogic = new GameLogic(size, maxPlayer);
        private boolean gameEnd = false;
        private static final Color[]  playerColor = {null, Color.BLACK, Color.WHITE, Color.RED, Color.BLUE};
        private static final String[] playerName  = {null, "BLACK", "WHITE-AI", "RED-AI", "BLUE-AI"};
        /***
         * Setter koji koristimo prilikom promjene veličine ploče pri pokretanju nove igre.
         * @param val - vrijednost nove veličine ploče
         */
        public void setVals(int val) {
            size = val;
            sizeLen = size * cellLen;
        }
        /***
         * Setter koji koristimo prilikom promjene količine igrača pri pokretanju nove igre.
         * @param val - vrijednost novog broja igrača
         */
        public void setPlayers(int val) { maxPlayer = val; }
        /***
         * Konstruktor klase GameGUI. Priprema komponente za prikaz unutar okvira.
         */
        public GameGUI() {
            /*
            Kreiranje menija igre. Njegovo popunjavanje te pozicioniranje la lijevoj strani
            gornjeg dijela layouta budućeg panela.
            */
            JMenuBar menuBar = new JMenuBar();
            JRadioButtonMenuItem smallBoard, normalBoard, largeBoard, twoPlayer, threePlayer, fourPlayer;
            JMenu fileMenu = new JMenu("New Game");
            /*
            Kreiranje radio gumba za meni grupiranih u dvije grupe. Unutar svake grupe aktivan je samo jedan gumb.
            Prvu grupu čine postavljači veličine ploče. Drugu, postavljači broja igrača.
             */
            smallBoard = new JRadioButtonMenuItem("13x13 board");
            smallBoard.setMnemonic(KeyEvent.VK_R);
            normalBoard = new JRadioButtonMenuItem("19x19 board");
            normalBoard.setMnemonic(KeyEvent.VK_R);
            largeBoard = new JRadioButtonMenuItem("25x25 board");
            largeBoard.setSelected(true);
            largeBoard.setMnemonic(KeyEvent.VK_R);
            twoPlayer = new JRadioButtonMenuItem("2 players");
            twoPlayer.setSelected(true);
            twoPlayer.setMnemonic(KeyEvent.VK_R);
            threePlayer = new JRadioButtonMenuItem("3 players");
            threePlayer.setMnemonic(KeyEvent.VK_R);
            fourPlayer = new JRadioButtonMenuItem("4 players");
            fourPlayer.setMnemonic(KeyEvent.VK_R);
            ButtonGroup group1 = new ButtonGroup();
            group1.add(smallBoard);
            fileMenu.add(smallBoard);
            group1.add(normalBoard);
            fileMenu.add(normalBoard);
            group1.add(largeBoard);
            fileMenu.add(largeBoard);
            fileMenu.addSeparator();
            ButtonGroup group2 = new ButtonGroup();
            group2.add(twoPlayer);
            fileMenu.add(twoPlayer);
            group2.add(threePlayer);
            fileMenu.add(threePlayer);
            group2.add(fourPlayer);
            fileMenu.add(fourPlayer);
            menuBar.add(fileMenu);
            JPanel controlPanel = new JPanel();
            controlPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
            controlPanel.add(menuBar);
            /*
            Kreiranje ploče igre u panelu te dodavanje prethodnih elemenata menija te tekstualnog polja.
            */
            boardDisplay = new GraphicsPanel();
            this.setLayout(new BorderLayout());
            this.add(controlPanel , BorderLayout.NORTH);
            this.add(boardDisplay, BorderLayout.CENTER);
            statusField.setEditable(false);
            this.add(statusField , BorderLayout.SOUTH);
            /*
            Stavljanje action listenera na gumbe unutar menija.
            */
            smallBoard.addActionListener(new smallBoardAction());
            normalBoard.addActionListener(new normalBoardAction());
            largeBoard.addActionListener(new largeBoardAction());
            twoPlayer.addActionListener(new twoPlayerAction());
            threePlayer.addActionListener(new threePlayerAction());
            fourPlayer.addActionListener(new fourPlayerAction());
            /*
            Prikaz uvodne info kutije.
             */
            String onOpen = "<html><body><div width='250px' align='center'> 5 in a Row, igra je nadahnuta " +
                    "mobilnim igrama sa starih Sony Ericsson mobitela.<br><br>Igra se sastoji od ploče na kojoj " +
                    "naizmjence igrači postavljaju svoje figurice. Prvi igrač koji postavi 5 svojih figurica " +
                    "u neprekinuti niz je pobjednik.<br><br>Igra dopušta igranje na maloj, srednjoj i velikoj ploči. " +
                    "Igranjem počinje igrač nakon čega se izmjenjuju od 1 do 3 računalnih igrača pokretanih " +
                    "jednostavnom inteligencijom.<br><br>Veličinu ploče te broj igrača korisnik sam može namještati " +
                    "kroz 'New Game' meni.<br><br>----------<br><br>Igru je napravio student prve godine diplomskog " +
                    "studija Marko Gabrovec za kolegij Suvremene tehnike programiranja 2020/21.</div></body></html>";
            JLabel messageLabel = new JLabel(onOpen);
            final JOptionPane optionPane = new JOptionPane(messageLabel,
                    JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION,
                    null);
            final JDialog dialog = new JDialog();
            dialog.setModal(true);
            dialog.setContentPane(optionPane);
            dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
            dialog.pack();
            dialog.setLocationRelativeTo(boardDisplay);
            Object[] options = { "OK" };
            JOptionPane.showOptionDialog(dialog, messageLabel,
                    "5 in a Row - v0.1", JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION,
                    null, options, options[0]);

        }
        /***
         * Klasa zadužena za sam panel igre.
         */
        class GraphicsPanel extends JPanel implements MouseListener {
            /***
             * Konstruktor klase panela. Postavlja željene dimenzije panela,
             * pričuvnog backgrounda i mouse listenera.
             */
            public GraphicsPanel() {
                this.setPreferredSize(new Dimension(sizeLen, sizeLen));
                this.setBackground(Color.lightGray);
                this.addMouseListener(this);  // listener na mouse evente samog objekta
            }
            /***
             * Metoda za crtanje grafičkom dretvom. Overridea se običan paintComponent kako bi činio ono što želimo.
             * Sama metoda se često poziva skrivena u drugim metodama kao što je repaint().
             * @param g - grafički objekt koji se vrti na svojoj dretvi
             */
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                /*
                Učitavanje slike i postavljanje iste za pozadinu igre.
                */
                URL url = ClassLoader.getSystemResource("wood-68.jpg");
                //File imgLoc = new File("resources/wood-68.jpg");
                BufferedImage img;
                try {
                    img = ImageIO.read(url);
                    g.drawImage(img, 0, 0, null);
                } catch (IOException ex) {
                    System.err.println(ex.getMessage());
                    ex.printStackTrace();
                }
                /*
                Stvaranje lista brojčanih i numeričkih znakova za označavanje redaka i stupaca na ploči.
                */
                int a = 65;
                char[] listChar = new char[size];
                int[] listNum = new int[size];
                for (int i = 0; i < size; i++) {
                    listChar[i] = (char) (a + i);
                    listNum[i] = i + 1;
                }
                /*
                Crtač linija i znakova na ploču.
                */
                Graphics2D g2d = (Graphics2D) g;
                g2d.setStroke(new BasicStroke(1.5F));
                for (int r = 1; r <= size; r++) {  //horizontalne linije i brojke uz liniju
                    g2d.drawString(String.valueOf(listNum[size - r]), 0, r * cellLen - 10);
                    g2d.drawLine(15, r * cellLen - 15, sizeLen - 15, r * cellLen - 15);
                }
                for (int c = 1; c <= size; c++) { //vertikalne linije i slova uz liniju
                    g2d.drawString(String.valueOf(listChar[c - 1]), c * cellLen - 18, 10);
                    g2d.drawLine(c * cellLen - 15, 0 + 15, c * cellLen - 15, sizeLen - 15);
                }
                /*
                Stvaranje 9 točaka vodilja kao na GO ploči.
                Te točke se različito stvaraju ovisno o postavljenoj veličini ploče.
                */
                int first, center, last;
                g.setColor(Color.BLACK);
                if (size == 13) {
                    first = cellLen * (2) + 10;
                    center = cellLen * (6) + 10;
                    last = cellLen * (10) + 10;
                } else if (size == 19) {
                    first = cellLen * (3) + 10;
                    center = cellLen * (9) + 10;
                    last = cellLen * (15) + 10;
                } else {
                    first = cellLen * (5) + 10;
                    center = cellLen * (12) + 10;
                    last = cellLen * (19) + 10;
                }
                g.fillOval(first, first, 10, 10);
                g.fillOval(first, center, 10, 10);
                g.fillOval(first, last, 10, 10);
                g.fillOval(center, first, 10, 10);
                g.fillOval(center, center, 10, 10);
                g.fillOval(center, last, 10, 10);
                g.fillOval(last, first, 10, 10);
                g.fillOval(last, center, 10, 10);
                g.fillOval(last, last, 10, 10);
                /*
                Postavljanje pravilne veličine same ploče.
                 */
                window.setSize(sizeLen + 15, sizeLen + 105);
                /*
                Crtanje figura na ploču.
                 */
                for (int r = 0; r < size; r++) {
                    for (int c = 0; c < size; c++) {
                        int x = c * cellLen;
                        int y = r * cellLen;
                        int who = gameLogic.getPlayerAt(r, c);
                        if (who != gameLogic.EMPTY) { // Ako nije prazno postavi boju i crtaj.
                            g.setColor(playerColor[who]);
                            g.fillOval(x + 2, y + 2, cellLen - 4, cellLen - 4);
                        }
                    }
                }
                /*
                Označavanje zadnjeg poteza zelenim kružićem.
                 */
                g2d.setColor(Color.GREEN);
                if (gameLogic.getMoves() > 1) {
                    g.fillOval(((gameLogic.getLastY() * cellLen) + 11), ((gameLogic.getLastX() * cellLen) + 11), 8, 8);
                }
                /*
                Označavanje ostvarenog niza od 5 istih figura zelenom linijom.
                 */
                g2d.setStroke(new BasicStroke(4F));
                if (gameEnd == true) {
                    g2d.drawLine(gameLogic.getPocZav()[0][0] * cellLen + 15, gameLogic.getPocZav()[0][1] * cellLen + 15,
                            gameLogic.getPocZav()[1][0] * cellLen + 15, gameLogic.getPocZav()[1][1] * cellLen + 15);
                }
                /*
                Inicijalno postavljanje vrijednosti tekstualnog polja.
                 */
                if (gameLogic.getMoves() == 0) {
                    statusField.setText(playerName[1] + " starts the game.");
                }
            }

            /***
             * Metoda odgovora na akciju miša.
             * @param e - akcija miša
             */
            public void mousePressed(MouseEvent e) {
                if(!gameEnd && e == null) { // e == null, samo pri pozivu bez klika. Pokretan umjetnom inteligencijom.
                    gameLogic.move(0, 0);
                    this.repaint();
                    switch (gameLogic.getGameStatus()) {
                        case 1: // Pobjedio prvi. Nema break, idi dalje.
                        case 2: // Pobjedio drugi. Nema break, idi dalje.
                        case 3: // Pobjedio treći. Nema break, idi dalje.
                        case 4: // Pobjedio peti. Nema break, idi dalje.
                        case GameLogic.TIE:  // Rezultat neriješen. Prekini igru.
                            gameEnd = true;
                            break;
                    }
                } else { // e != null, pri kliku miša.
                    /*
                    Odredi dijeljenjem x i y koordinata sa veličinom jednog polja,
                    čeliju ploče koja je pritisnuta mišem.
                     */
                    int col = e.getX()/cellLen;
                    int row = e.getY()/cellLen;
                    int currentOccupant = gameLogic.getPlayerAt(row, col);
                    if (!gameEnd && currentOccupant == gameLogic.EMPTY) {
                        gameLogic.move(row, col);
                        switch (gameLogic.getGameStatus()) {
                            case 1: // Pobjedio prvi. Nema break, idi dalje.
                            case 2: // Pobjedio drugi. Nema break, idi dalje.
                            case 3: // Pobjedio treći. Nema break, idi dalje.
                            case 4: // Pobjedio peti. Nema break, idi dalje.
                            case GameLogic.TIE:  // Rezultat neriješen. Prekini igru.
                                gameEnd = true;
                                break;
                        }
                    } else { // Pusti zvuk ako je pritisnut nelegalni potez.
                        Toolkit.getDefaultToolkit().beep();
                    }
                }
                this.repaint();
                switch (gameLogic.getGameStatus()) {
                    case 1: // Pobjedio prvi. Pokreni dijalog pobjede.
                        JOptionPane.showMessageDialog(this, "BLACK WINS");
                        break;
                    case 2: // Pobjedio drugi. Pokreni dijalog pobjede.
                        JOptionPane.showMessageDialog(this, "WHITE WINS");
                        break;
                    case 3: // Pobjedio treći. Pokreni dijalog pobjede.
                        JOptionPane.showMessageDialog(this, "RED WINS");
                        break;
                    case 4: // Pobjedio četvrti. Pokreni dijalog pobjede.
                        JOptionPane.showMessageDialog(this, "BLUE WINS");
                        break;
                    case GameLogic.TIE: // Rezultat neriješen. Prekini igru.
                        JOptionPane.showMessageDialog(this, "TIE GAME");
                        break;
                    default:
                        showNextPlayer();
                }
            }
            public void mouseClicked (MouseEvent e) {}
            public void mouseReleased(MouseEvent e) {}
            public void mouseEntered (MouseEvent e) {}
            public void mouseExited  (MouseEvent e) {}
        }
        /***
         * Pomočna metoda za prebacivanje igrača te uređenje vizualija prijelaza.
         */
        private void showNextPlayer() {
            /*
            Postavljanje tekstualnog polja statusa igre i pripreme modalnog dijalog prozora za prikaz prijelaza poteza.
             */
            statusField.setText(playerName[gameLogic.getNextPlayer()] + " playing");
            String message = "<html><body><div width='200px' align='center'>" + "TURN " +
                    (gameLogic.getMoves()/maxPlayer+1) + "<br>-----<br>" +
                   playerName[gameLogic.getNextPlayer()] + "s turn</div></body></html>";
            JLabel messageLabel = new JLabel(message);
            final JOptionPane optionPane = new JOptionPane(messageLabel,
                    JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION,
                    null, new Object[]{}, null);
            final JDialog dialog = new JDialog();
            dialog.setUndecorated(true);
            dialog.setTitle("Turn " + (gameLogic.getMoves()/maxPlayer+1));
            dialog.setModal(true);
            dialog.setContentPane(optionPane);
            dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
            dialog.pack();
            dialog.setLocationRelativeTo(boardDisplay);
            Timer timer = new Timer(2000, new AbstractAction() { // Novi timer od 2000ms-a. Pokrenut 1 put.
                @Override
                public void actionPerformed(ActionEvent ae) {
                    dialog.dispose(); // Nakon 2000ms-a miče se modalni dijalog.
                    if(gameLogic.getNextPlayer() != 1) { // Ako se ne radi o igraču probaj...
                        try{
                            TimeUnit.SECONDS.sleep(2); // Pauziraj izvođenje na 2s-e.
                            boardDisplay.mousePressed(null); // Pozivanje mouse listener eventa sa null vrijednošću.
                        }
                        catch (InterruptedException e){ System.out.println("Await exception."); }
                    }
                }
            });
            timer.setRepeats(false);
            timer.start();
            dialog.setVisible(true);
        }
        /***
         * Metoda za pokretanje igre sa 13x13 pločom.
         */
        private class smallBoardAction implements ActionListener {
            public void actionPerformed(ActionEvent e) {
                setVals(13);
                gameLogic.reset(size, maxPlayer);
                gameEnd = false;
                boardDisplay.revalidate();
                boardDisplay.repaint();
                showNextPlayer();
            }
        }
        /***
         * Metoda za pokretanje igre sa 19x19 pločom.
         */
        private class normalBoardAction implements ActionListener {
            public void actionPerformed(ActionEvent e) {
                setVals(19);
                gameLogic.reset(size, maxPlayer);
                gameEnd = false;
                boardDisplay.revalidate();
                boardDisplay.repaint();
                showNextPlayer();
            }
        }
        /***
         * Metoda za pokretanje igre sa 25x25 pločom.
         */
        private class largeBoardAction implements ActionListener {
            public void actionPerformed(ActionEvent e) {
                setVals(25);
                gameLogic.reset(size, maxPlayer);
                gameEnd = false;
                boardDisplay.revalidate();
                boardDisplay.repaint();
                showNextPlayer();
            }
        }
        /***
         * Metoda za pokretanje igre sa 2 igrača.
         */
        private class twoPlayerAction implements ActionListener {
            public void actionPerformed(ActionEvent e) {
                setPlayers(2);
                gameLogic.reset(size, maxPlayer);
                gameEnd = false;
                boardDisplay.revalidate();
                boardDisplay.repaint();
                showNextPlayer();
            }
        }
        /***
         * Metoda za pokretanje igre sa 3 igrača.
         */
        private class threePlayerAction implements ActionListener {
            public void actionPerformed(ActionEvent e) {
                setPlayers(3);
                gameLogic.reset(size, maxPlayer);
                gameEnd = false;
                boardDisplay.revalidate();
                boardDisplay.repaint();
                showNextPlayer();
            }
        }
        /***
         * Metoda za pokretanje igre sa 4 igrača.
         */
        private class fourPlayerAction implements ActionListener {
            public void actionPerformed(ActionEvent e) {
                setPlayers(4);
                gameLogic.reset(size, maxPlayer);
                gameEnd = false;
                boardDisplay.revalidate();
                boardDisplay.repaint();
                showNextPlayer();
            }
        }

    }
}